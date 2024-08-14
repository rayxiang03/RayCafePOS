package my.ray.app.view

import scalafx.scene.control.{Alert, Button, Label, Spinner, SpinnerValueFactory}
import scalafx.scene.image.ImageView
import scalafxml.core.macros.sfxml
import scalafx.stage.Stage
import my.ray.app.model.Product
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory
import scalafxml.core.{FXMLLoader, NoDependencyResolver}
import javafx.{scene => jfxs}
import my.ray.app.MainApp

@sfxml
class ProductCardController(
                             private val productImage: ImageView,
                             private val productName: Label,
                             private val productPrice: Label,
                             private val productDescription: Label,
                             private val productStock: Label,
                             private val productSpinner: Spinner[Int],
                             private val actionButton: Button,
                           ) {

  private var _product: Product = _
  var dialogStage: Stage = _
  var okClicked = false
  var mode: String = MainApp.productCardMode
  var tempQty: Int = _

  def product = _product

  def product_=(x: Product) {
    _product = x

    productName.text = _product.nameProperty.value
    productPrice.text = _product.priceProperty.value.toString
    productDescription.text = _product.descriptionProperty.value
    productImage.image = _product.imageProperty.value
    productStock.text = _product.stockProperty.value.toString

    // Set the Spinner's minimum and maximum values based on the stock variable
    val min = if (mode == "update") 0 else 1
    val max = _product.stockProperty.value

    val spinnerValueFactory = new IntegerSpinnerValueFactory(min, max)
    productSpinner.valueFactory = spinnerValueFactory.asInstanceOf[SpinnerValueFactory[Int]]

    if (mode == "update") {
      // Ensure tempQty is within the allowed range
      if (tempQty < min) tempQty = min
      if (tempQty > max) tempQty = max
      spinnerValueFactory.setValue(tempQty)
    } else {
      spinnerValueFactory.setValue(min)
    }

    // Add listener to handle manual input changes
    productSpinner.getEditor.textProperty().addListener { (_, _, newValue) =>
      try {
        val value = newValue.toInt
        if (value > max || value < min) {
          val alert = new Alert(AlertType.Warning) {
            initOwner(dialogStage)
            title = "Warning"
            headerText = "Invalid Stock Value"
            contentText = "The stock value must be between " + min + " and " + max + "."
          }
          alert.showAndWait()
          if (value > max) {
            productSpinner.getValueFactory.setValue(max)
          } else if (value < min) {
            productSpinner.getValueFactory.setValue(min)
          }
        }
      } catch {
        case _: NumberFormatException =>
          productSpinner.getValueFactory.setValue(min)
      }
    }
    handleAddUpdate()
  }


  def handleAddUpdate(): Unit = {
    mode match {
      case "add" =>
        actionButton.text = "ADD TO ORDER"
        actionButton.onAction = _ => addUpdateOrder("add")
      case "update" =>
        actionButton.text = "UPDATE"
        actionButton.onAction = _ => addUpdateOrder("update")

    }
  }

  def addUpdateOrder(mode: String): Unit = {
    val selectedQuantity = productSpinner.getValue

    val resource = getClass.getResource("../view/Order.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    val root = loader.load[jfxs.Parent]
    val orderController = loader.getController[OrderController#Controller]
    val currentOrderQty = orderController.getProductQuantityInOrder(_product)

    mode match {
      case "add" if selectedQuantity > 0 && selectedQuantity <= _product.stockProperty.value  - currentOrderQty =>
        orderController.addProductToOrder(_product, selectedQuantity)

        val alert = new Alert(AlertType.Information) {
          initOwner(dialogStage)
          title = "Success"
          headerText = "Product Added to Order"
          contentText = s"${_product.nameProperty.value} has been added with quantity $selectedQuantity."
        }
        alert.showAndWait()
        okClicked = true
        dialogStage.close()

      case "update" =>
        if (selectedQuantity == 0) {
          orderController.removeProductFromOrder(_product)
          val alert = new Alert(AlertType.Information) {
            initOwner(dialogStage)
            title = "Success"
            headerText = "Product Removed from Order"
            contentText = s"${_product.nameProperty.value} has been removed from the order."
          }
          alert.showAndWait()
          okClicked = true
          dialogStage.close()

        } else if (selectedQuantity > 0 && selectedQuantity <= _product.stockProperty.value) {
          orderController.updateProductInOrder(_product, selectedQuantity)

          val alert = new Alert(AlertType.Information) {
            initOwner(dialogStage)
            title = "Success"
            headerText = "Product Updated"
            contentText = s"${_product.nameProperty.value} has been updated with quantity $selectedQuantity."
          }
          alert.showAndWait()
          okClicked = true
          dialogStage.close()

        }

      case _ =>
        val alert = new Alert(AlertType.Error) {
          initOwner(dialogStage)
          title = "Error"
          headerText = "Exceed Maximum Limit For This Product"
          contentText = s"Cannot add/update ${_product.nameProperty.value} to the order. Excedeed Limit per order. Please modify the quantity."
        }
        alert.showAndWait()
    }
  }

  def handleClose(): Unit = {
    dialogStage.close()
  }

}

