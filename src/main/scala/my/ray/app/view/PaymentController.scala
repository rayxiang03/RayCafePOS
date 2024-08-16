package my.ray.app.view

import my.ray.app.MainApp
import scalafx.scene.control.{Alert, ButtonType, Label, TableColumn, TableView, TextField, ToggleGroup}
import scalafx.stage.Stage
import my.ray.app.model.{OrderTransaction, Product}
import scalafx.application.Platform
import scalafx.beans.property.{ObjectProperty, StringProperty}
import scalafxml.core.macros.sfxml

import scala.collection.convert.ImplicitConversions.`collection AsScalaIterable`
import scala.util.Try

@sfxml
class PaymentController(
                         private val checkOrderTable: TableView[(Product, Int)],
                         private val numberColumn: TableColumn[(Product, Int), Int],
                         private val idColumn: TableColumn[(Product, Int), String],
                         private val productColumn: TableColumn[(Product, Int), String],
                         private val quantityColumn: TableColumn[(Product, Int), Int],
                         private val priceColumn: TableColumn[(Product, Int), String],
                         private val subTotal: Label,
                         private val serviceCharge: Label,
                         private val sstCharge: Label,
                         private val total: Label,
                         private val paymentAmountField: TextField,
                         private val paymentMethod: ToggleGroup,
                         private val changeAmountField: TextField
                       ) {

  var paymentStage: Stage = _

  // Set up table columns
  numberColumn.cellValueFactory = cellData => ObjectProperty(checkOrderTable.getItems.indexOf(cellData.value) + 1)
  idColumn.cellValueFactory = cellData => StringProperty(cellData.value._1.id)
  productColumn.cellValueFactory = cellData => StringProperty(cellData.value._1.name)
  quantityColumn.cellValueFactory = cellData => ObjectProperty(cellData.value._2)
  priceColumn.cellValueFactory = { cellData =>
    val totalPrice = cellData.value._1.price * cellData.value._2
    StringProperty(f"$totalPrice%.2f")
  }

  paymentMethod.selectedToggle.onChange { (_, _, newToggle) =>
    Option(newToggle).collect {
      case rb: javafx.scene.control.RadioButton => rb.getId
    } match {
      case Some("eWalletRadioButton") =>
        paymentAmountField.disable = true
        paymentAmountField.text = total.text.value
      case Some("cashRadioButton") =>
        paymentAmountField.disable = false
        paymentAmountField.text = "" // Clear payment amount
      case _ =>
        println("No matching toggle found")
    }
  }

  paymentAmountField.text.onChange { (_, _, newValue) =>
    val payableAmount = Try(total.text.value.toDouble).getOrElse(0.0)
    val paymentAmount = Try(newValue.toDouble).getOrElse(0.0)

    if (paymentAmount >= payableAmount) {
      val changeAmount = paymentAmount - payableAmount
      changeAmountField.text = f"$changeAmount%.2f"
    } else {
      changeAmountField.text = "0.00"
    }
  }


  def setOrderItems(orderItems: List[(Product, Int)]): Unit = {
    checkOrderTable.getItems.setAll(orderItems: _*)
  }

  def setAmounts(subTotalValue: Double, serviceChargeValue: Double, sstValue: Double, totalValue: Double): Unit = {
    subTotal.setText(f"$subTotalValue%.2f")
    serviceCharge.setText(f"$serviceChargeValue%.2f")
    sstCharge.setText(f"$sstValue%.2f")
    total.setText(f"$totalValue%.2f")
  }

  def handleReturnBack(): Unit = {
    paymentStage.close()
  }

  def handleConfirmPayment(): Unit = {
    val payableAmount = total.text.value.toDouble
    val paymentAmount = Try(paymentAmountField.text.value.toDouble).getOrElse(0.0)

    if (paymentAmount < payableAmount) {
      val alert = new Alert(Alert.AlertType.Warning) {
        initOwner(MainApp.stage)
        title = "Insufficient Payment"
        headerText = "Payment amount is less than the total amount"
        contentText = "Please enter a valid payment amount."
      }
      alert.showAndWait()
      return
    }

    val confirmationAlert = new Alert(Alert.AlertType.Confirmation) {
      initOwner(MainApp.stage)
      title = "Confirm Payment"
      headerText = "Are you sure you want to proceed with the payment?"
      contentText = "Please confirm your action."
    }

    val result = confirmationAlert.showAndWait()
    if (result.contains(ButtonType.OK)) {
      val orderItems = checkOrderTable.getItems.map { case (product, quantity) =>
        (product, quantity, product.price * quantity)
      }.toList


      val order = new OrderTransaction(
        orderItems,
        subTotal.text.value.toDouble,
        serviceCharge.text.value.toDouble,
        sstCharge.text.value.toDouble,
        total.text.value.toDouble,
        paymentMethod.selectedToggle.value.asInstanceOf[javafx.scene.control.RadioButton].getText,
        paymentAmountField.text.value.toDouble,
        changeAmountField.text.value.toDouble,
        MainApp.isTakeAwayChecked.toString.toBoolean
      )

      // Save the order
      val orderId = order.save()
      MainApp.currentOrderItems.clear()
      paymentStage.close()

      // Ensure that it work smoothly after close the payment stage
      Platform.runLater {
        MainApp.showReceipt(orderId)
      }
    }
  }
}