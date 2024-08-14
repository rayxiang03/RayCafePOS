package my.ray.app.model

import scalafx.beans.property.{ObjectProperty, StringProperty}
import scalafx.scene.image.Image


class Beverage(
                _id: String,
                _name: String,
                _price: Double,
                _description: String,
                _stock: Int,
                _imagePath: Image,
                _status: String
              ) extends Product(_id, _name, _price, _description, _stock, _imagePath, _status) with Consumable {

}

// Companion object for Beverage
object Beverage{

    val mangoSmoothie: Beverage = new Beverage(
      "B001",
      "Mango Smoothie",
      5.99,
      "A delicious and refreshing mango smoothie",
      10,
      new Image(getClass.getResourceAsStream("/images/mango_smoothie.png")),
      "Active")

    val moccaccino: Beverage = new Beverage(
      "B002",
      "Moccaccino",
      4.99,
      "A delicious and refreshing moccaccino",
      10,
      new Image(getClass.getResourceAsStream("/images/moccaccino.png")),
      "Active")

    val iceLatte: Beverage = new Beverage(
      "B003",
      "Ice Latte",
      3.99,
      "A delicious and refreshing ice latte",
      10,
      new Image(getClass.getResourceAsStream("/images/iced-latte.png")),
      "Active")

    val oatmilkMocha: Beverage = new Beverage(
      "B004",
      "Oatmilk Mocha",
      4.99,
      "A delicious and refreshing oatmilk mocha",
      10,
      new Image(getClass.getResourceAsStream("/images/Oatmilk-Mocha-Flash-Brew.png")),
      "Active")


  // List of all predefined beverages
  val beverages: List[Beverage] = List(mangoSmoothie, moccaccino, iceLatte, oatmilkMocha)

  // Method to get all beverages
  def getAllBeverages: List[Beverage] = {
    beverages
  }


}