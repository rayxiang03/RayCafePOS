package my.ray.app.view

import scalafx.scene.control.{Alert, Button, Label, Spinner, SpinnerValueFactory}
import scalafx.scene.image.ImageView
import scalafxml.core.macros.sfxml
import scalafx.stage.Stage
import my.ray.app.model.Product
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory
import my.ray.app.view.OrderController
import scalafxml.core.{FXMLLoader, NoDependencyResolver}
import javafx.{scene => jfxs}

@sfxml
class ProductCardController(
                             private val productImage: ImageView,
                             private val productName: Label,
                             private val productPrice: Label,
                             private val productDescription: Label,
                             private val productStock: Label,
                             private val productSpinner: Spinner[Int],
                             private val addOrderBtn: Button,
                           ) {

  var dialogStage: Stage = null
  var okClicked = false
  private var _product: Product = null

  def product = _product

  def product_=(x: Product) {
    _product = x

    productName.text = _product.nameProperty.value
    productPrice.text = _product.priceProperty.value.toString
    productDescription.text = _product.descriptionProperty.value
    productImage.image = _product.imageProperty.value
    productStock.text = _product.stockProperty.value.toString

    // Set the Spinner's minimum and maximum values based on the stock variable
    val min = 1
    val max = _product.stockProperty.value

    val spinnerValueFactory = new IntegerSpinnerValueFactory(min, max)
    spinnerValueFactory.setValue(min)
    productSpinner.valueFactory = spinnerValueFactory.asInstanceOf[SpinnerValueFactory[Int]]


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
  }


  def addOrder(): Unit = {
    val selectedQuantity = productSpinner.getValue
    if (selectedQuantity <= _product.stockProperty.value) {
      // Load OrderController using FXMLLoader
      val resource = getClass.getResource("../view/Order.fxml")
      val loader = new FXMLLoader(resource, NoDependencyResolver)
      val root = loader.load[jfxs.Parent]
      val orderController = loader.getController[OrderController#Controller]

      // Call method on OrderController
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
    } else {
      val alert = new Alert(AlertType.Error) {
        initOwner(dialogStage)
        title = "Error"
        headerText = "Insufficient Stock"
        contentText = s"Cannot add ${_product.nameProperty.value} to the order. Not enough stock."
      }
      alert.showAndWait()
    }
  }

  def handleClose(): Unit = {
    dialogStage.close()
  }

}

