package my.ray.app.model

import my.ray.app.util.{Database, SessionManager}
import scalafx.beans.property._
import scalafx.collections.ObservableBuffer
import scalikejdbc._

import java.util.UUID
import java.sql.SQLException
import java.time.{LocalDate, LocalDateTime}

case class OrderTransaction(
                             orderItemsS: List[(Product, Int, Double)],
                             subTotalS: Double,
                             serviceChargeS: Double,
                             sstChargeS: Double,
                             totalS: Double,
                             paymentMethodS: String,
                             paymentAmountS: Double,
                             changeAmountS: Double,
                             isTakeAwayS: Boolean,
                           ) extends Database {

  def this() = this(List.empty, 0.0, 0.0, 0.0, 0.0, null, 0.0, 0.0, false)

  var orderId = StringProperty(UUID.randomUUID().toString)
  var orderItems: ObjectProperty[ObservableBuffer[(Product, Int, Double)]] = ObjectProperty(ObservableBuffer(orderItemsS: _*))
  var subTotal = DoubleProperty(subTotalS)
  var serviceCharge = DoubleProperty(serviceChargeS)
  var sstCharge = DoubleProperty(sstChargeS)
  var total = DoubleProperty(totalS)
  var paymentMethod = StringProperty(paymentMethodS)
  var paymentAmount = DoubleProperty(paymentAmountS)
  var changeAmount = DoubleProperty(changeAmountS)
  var isTakeAway = BooleanProperty(isTakeAwayS)
  var salesDate = ObjectProperty[LocalDateTime](LocalDateTime.now())
  var createdBy = ObjectProperty(SessionManager.getCurrentUser.map(_.userId).getOrElse("Unknown"))
  var status = StringProperty("ACTIVE")

  def save(): String = {
    saveOrderHeader()
    saveOrderDetails()

    //Return orderID for display receipt
    orderId.value
  }

  private def saveOrderHeader(): Unit = {
    DB autoCommit { implicit session =>
      sql"""
      insert into Txn_order_header (
        order_id, sub_total, service_charge, sst_charge, total, payment_method, payment_amount, change_amount, sales_date, is_take_away, created_by, status
      ) values (
        ${orderId.value}, ${subTotal.value}, ${serviceCharge.value}, ${sstCharge.value}, ${total.value}, ${paymentMethod.value}, ${paymentAmount.value}, ${changeAmount.value}, ${salesDate.value}, ${isTakeAway.value}, ${createdBy.value}, ${status.value}
      )
      """.update.apply()
    }
  }

  private def saveOrderDetails(): Unit = {
    DB autoCommit { implicit session =>
      orderItems.value.foreach { case (product, quantity, price) =>
        sql"""
        insert into Txn_order_detail (
          order_id, product_id, product_name, quantity, price
        ) values (
          ${orderId.value}, ${product.id}, ${product.name}, ${quantity}, ${price}
        )
        """.update.apply()
      }
    }
  }
}

object OrderTransaction extends Database {
  def apply(
             orderIdS: String,
             orderItemsS: List[(Product, Int, Double)],
             subTotalS: Double,
             serviceChargeS: Double,
             sstChargeS: Double,
             totalS: Double,
             paymentMethodS: String,
             paymentAmountS: Double,
             changeAmountS: Double,
             isTakeAwayS: Boolean,
             salesDateS: LocalDateTime,
             createdByS: User
           ): OrderTransaction = {

    new OrderTransaction(
      orderItemsS,
      subTotalS,
      serviceChargeS,
      sstChargeS,
      totalS,
      paymentMethodS,
      paymentAmountS,
      changeAmountS,
      isTakeAwayS,
    ) {
      orderId.value = orderIdS
      salesDate.value = salesDateS
      createdBy.value = createdByS.userId
    }
  }

  def initializeTable(): Unit = {
    DB autoCommit { implicit session =>
      try {
        sql"""
        create table Txn_order_header (
          order_id varchar(50) not null primary key,
          sub_total double,
          service_charge double,
          sst_charge double,
          total double,
          payment_method varchar(20),
          payment_amount double,
          change_amount double,
          is_take_away boolean,
          sales_date timestamp,
          created_by varchar(50) not null,
          status varchar(20),
          foreign key (created_by) references Users(user_id)
        )
        """.execute.apply()

        sql"""
        create table Txn_order_detail (
          order_id varchar(50) not null,
          product_id varchar(50) not null,
          product_name varchar(50),
          quantity int,
          price double,
          foreign key (order_id) references Txn_order_header(order_id)
        )
        """.execute.apply()
      } catch {
        case e: SQLException if e.getSQLState == "X0Y32" =>
        case e: SQLException =>
          println(s"SQL Error: ${e.getMessage}")
      }
    }
  }

  def findById(orderId: String): Option[OrderTransaction] = {
    DB readOnly { implicit session =>
      val header =
        sql"""
        select * from Txn_order_header where order_id = $orderId and status = 'ACTIVE'
      """.map(rs => (
          rs.string("order_id"),
          rs.double("sub_total"),
          rs.double("service_charge"),
          rs.double("sst_charge"),
          rs.double("total"),
          rs.string("payment_method"),
          rs.double("payment_amount"),
          rs.double("change_amount"),
          rs.boolean("is_take_away"),
          rs.localDateTime("sales_date"),
          rs.string("created_by")
        )).single.apply()

      val details =
        sql"""
        select * from Txn_order_detail where order_id = $orderId
      """.map(rs => (
          Product(rs.string("product_id"), rs.string("product_name"), rs.double("price"), "", 0, null, ""),
          rs.int("quantity"),
          rs.double("price")
        )).list.apply()

      header.map { case (orderId, subTotal, serviceCharge, sstCharge, total, paymentMethod, paymentAmount, changeAmount, isTakeAway, salesDate, createdBy) =>
        OrderTransaction(
          orderId,
          details,
          subTotal,
          serviceCharge,
          sstCharge,
          total,
          paymentMethod,
          paymentAmount,
          changeAmount,
          isTakeAway,
          salesDate,
          User(createdBy, "", "", "", LocalDateTime.now(), "", "", "", "", LocalDateTime.now(), LocalDateTime.now(), "")
        )
      }
    }
  }

  def getAllOrders(): List[OrderTransaction] = {
    DB readOnly { implicit session =>
      val headers =
        sql"""
        select * from Txn_order_header order by sales_date desc
      """.map(rs => (
          rs.string("order_id"),
          rs.double("sub_total"),
          rs.double("service_charge"),
          rs.double("sst_charge"),
          rs.double("total"),
          rs.string("payment_method"),
          rs.double("payment_amount"),
          rs.double("change_amount"),
          rs.boolean("is_take_away"),
          rs.localDateTime("sales_date"),
          rs.string("created_by")
        )).list.apply()

      headers.map { case (orderId, subTotal, serviceCharge, sstCharge, total, paymentMethod, paymentAmount, changeAmount, isTakeAway, salesDate, createdBy) =>
        val details =
          sql"""
          select * from Txn_order_detail where order_id = $orderId
        """.map(rs => (
            Product(rs.string("product_id"), rs.string("product_name"), rs.double("price"), "", 0, null, ""),
            rs.int("quantity"),
            rs.double("price")
          )).list.apply()

        OrderTransaction(
          orderId,
          details,
          subTotal,
          serviceCharge,
          sstCharge,
          total,
          paymentMethod,
          paymentAmount,
          changeAmount,
          isTakeAway,
          salesDate,
          User(createdBy, "", "", "", LocalDateTime.now(), "", "", "", "", LocalDateTime.now(), LocalDateTime.now(), "")
        )
      }
    }
  }


  def getTop10MostSoldItemsForDateRange(startDate: LocalDate, endDate: LocalDate): Map[String, Double] = {
    val startDateTime = startDate.atStartOfDay()
    val endDateTime = endDate.atTime(23, 59, 59)
    DB readOnly { implicit session =>
      sql"""
      SELECT product_name, SUM(quantity) as total_quantity
      FROM Txn_order_detail
      JOIN Txn_order_header ON Txn_order_detail.order_id = Txn_order_header.order_id
      WHERE sales_date >= $startDateTime AND sales_date <= $endDateTime
      GROUP BY product_name
      ORDER BY total_quantity DESC
      FETCH FIRST 8 ROWS ONLY
    """.map(rs => rs.string("product_name") -> rs.double("total_quantity")).list.apply().toMap
    }
  }

  def getSalesDataForDateRange(startDate: LocalDate, endDate: LocalDate): Map[Int, Double] = {
    val startDateTime = startDate.atStartOfDay()
    val endDateTime = endDate.atTime(23, 59, 59)
    DB readOnly { implicit session =>
      sql"""
      SELECT DAY(sales_date) as day, SUM(total) as sales
      FROM Txn_order_header
      WHERE sales_date >= $startDateTime AND sales_date <= $endDateTime
      GROUP BY DAY(sales_date)
    """.map(rs => rs.int("day") -> rs.double("sales")).list.apply().toMap
    }
  }

}