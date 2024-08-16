package my.ray.app.view

import my.ray.app.model.{OrderTransaction, Product}
import scalafx.beans.property.ObjectProperty
import scalafx.scene.control.{Label, TableColumn, TableView}
import scalafxml.core.macros.sfxml

import java.time.format.DateTimeFormatter
import scalafx.collections.ObservableBuffer

@sfxml
class ReceiptController(
                         private val orderNo: Label,
                         private val salesDate: Label,
                         private val subTotal: Label,
                         private val serviceCharge: Label,
                         private val sstCharge: Label,
                         private val total: Label,
                         private val payment: Label,
                         private val change: Label,
                         private val paymentMethod: Label,
                         private val productTable: TableView[(Product, Int, Double)],
                         private val itemColumn: TableColumn[(Product, Int, Double), String],
                         private val qtyColumn: TableColumn[(Product, Int, Double), Int],
                         private val totalColumn: TableColumn[(Product, Int, Double), String]
                       ) {
  def setOrderTransaction(orderTransaction: OrderTransaction): Unit = {
    orderNo.text = orderTransaction.orderId.value
    salesDate.text = orderTransaction.salesDate.value.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    subTotal.text = f"${orderTransaction.subTotal.value}%.2f"
    serviceCharge.text = f"${orderTransaction.serviceCharge.value}%.2f"
    sstCharge.text = f"${orderTransaction.sstCharge.value}%.2f"
    total.text = f"${orderTransaction.total.value}%.2f"
    payment.text = f"${orderTransaction.paymentAmount.value}%.2f"
    change.text = s"-${f"${orderTransaction.changeAmount.value}%.2f"}"
    paymentMethod.text = s"(${orderTransaction.paymentMethod.value})"

    // Populate the TableView
    productTable.items = ObservableBuffer(orderTransaction.orderItems.value: _*)

    itemColumn.cellValueFactory = cellData => ObjectProperty(s"${cellData.value._1.id} - ${cellData.value._1.name}")
    qtyColumn.cellValueFactory = cellData => ObjectProperty(cellData.value._2)
    totalColumn.cellValueFactory = cellData => ObjectProperty(f"${cellData.value._3}%.2f")

    productTable.setFixedCellSize(22)

    // Dynamically adjust the height based on the number of items
    val numberOfItems = productTable.items.get.size
    val rowHeight = productTable.fixedCellSize()
    val newHeight = rowHeight * (numberOfItems + 1.4)

    productTable.prefHeight = newHeight
    productTable.minHeight = newHeight
    productTable.maxHeight = newHeight
  }


}

