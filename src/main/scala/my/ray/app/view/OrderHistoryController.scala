package my.ray.app.view

import my.ray.app.MainApp
import my.ray.app.model.OrderTransaction
import scalafx.application.Platform
import scalafx.beans.property.ObjectProperty
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.{TableColumn, TableView}
import scalafxml.core.macros.sfxml

import java.net.URL
import java.time.format.DateTimeFormatter

@sfxml
class OrderHistoryController(
                              private val orderID: TableColumn[OrderTransaction, String],
                              private val total: TableColumn[OrderTransaction, Double],
                              private val saleDate: TableColumn[OrderTransaction, String],
                              private val cashierName: TableColumn[OrderTransaction, String],
                              private val orderHistoryTable: TableView[OrderTransaction],
                            ) {

  val orders = OrderTransaction.getAllOrders()
  val observableOrders = ObservableBuffer(orders: _*)
  orderHistoryTable.items = observableOrders

  orderID.cellValueFactory = { cellData =>
    ObjectProperty(cellData.value.orderId.value)
  }

  total.cellValueFactory = { cellData =>
    ObjectProperty(cellData.value.total.value)
  }

  saleDate.cellValueFactory = { cellData =>
    ObjectProperty(cellData.value.salesDate.value.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
  }

  cashierName.cellValueFactory = { cellData =>
    ObjectProperty(cellData.value.createdBy.value)
  }

  orderHistoryTable.selectionModel().selectedItemProperty().addListener { (_, _, selectedOrder) =>
    Platform.runLater {
      if (selectedOrder != null) {
        val orderId = selectedOrder.orderId.value
        MainApp.showReceipt(orderId)
      }
    }
  }


}
