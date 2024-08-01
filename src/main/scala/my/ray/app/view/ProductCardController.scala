package my.ray.app.view

import scalafx.scene.control.{Button, Label}
import scalafx.scene.layout.VBox
import scalafx.scene.image.ImageView
import scalafxml.core.macros.sfxml
import my.ray.app.model.Beverage
import scalafx.Includes._
import scalafx.scene.control.Label
import scalafx.stage.Stage
import my.ray.app.model.Product


@sfxml
class ProductCardController(
                             private val productImage: ImageView,
                             private val productName: Label,
                             private val productPrice: Label,
                             private val productDescription: Label
                           ) {


  var dialogStage: Stage = null
  var okClicked = false
  private var _product: Product = null

  def product = _product
  def product_=(x: Product) {
    _product = x

    productName.text = _product.nameProperty.value
    productPrice.text = _product.priceProperty.value
    productDescription.text = _product.descriptionProperty.value
    productImage.image = _product.imageProperty.value
  }

  def handleClose(): Unit = {
    dialogStage.close()
  }

}

