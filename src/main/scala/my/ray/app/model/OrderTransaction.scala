package my.ray.app.model

import my.ray.app.util.{Database, SessionManager}
import scalafx.beans.property._
import scalafx.collections.ObservableBuffer
import scalikejdbc._

import java.util.UUID
import java.sql.SQLException
import java.time.LocalDateTime

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
  var status =  StringProperty("ACTIVE")

  def save(): Unit = {
    saveOrderHeader()
    saveOrderDetails()
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
//      orderItems.value = ObservableBuffer(orderItemsS: _*)
//      subTotal.value = subTotalS
//      serviceCharge.value = serviceChargeS
//      sstCharge.value = sstChargeS
//      total.value = totalS
//      paymentMethod.value = paymentMethodS
//      paymentAmount.value = paymentAmountS
//      changeAmount.value = changeAmountS
//      isTakeAway.value = isTakeAwayS
      salesDate.value = salesDateS
//      createdBy.value = createdByS
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
          status varchar(20)
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
}