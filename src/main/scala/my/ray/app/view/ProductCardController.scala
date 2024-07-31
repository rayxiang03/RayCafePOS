package my.ray.app.view

import scalafx.scene.control.{Button, Label}
import scalafx.scene.layout.VBox
import scalafx.scene.image.ImageView
import scalafxml.core.macros.sfxml
import my.ray.app.model.Beverage
import scalafx.Includes._
import scalafx.scene.control.Label
import scalafx.stage.Stage


@sfxml
class ProductCardController(
                             private val productImage: ImageView,
                             private val productName: Label,
                             private val productPrice: Label,
                             private val productDescription: Label
                           ) {


  var dialogStage: Stage = null
  var okClicked = false
  private var _beverage: Beverage = null

  def beverage = _beverage
  def beverage_=(x: Beverage) {
    _beverage = x

    productName.text = _beverage.productName.value
    productPrice.text = _beverage.productPrice.value
    productDescription.text = _beverage.productDescription.value
    productImage.image = _beverage.productImage.value
  }
}

