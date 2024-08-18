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
      13.99,
      "A delicious and refreshing mango smoothie",
      3,
      new Image(getClass.getResourceAsStream("/images/mango_smoothie.png")),
      "Active")

    val mixBerrySmoothie: Beverage = new Beverage(
      "B001",
      "Mix Berry Smoothie",
      16.99,
      "This frozen berry smoothie is thick, creamy and full of delicious mixed berries.",
      3,
      new Image(getClass.getResourceAsStream("/images/berry_smoothie.jpg")),
      "Active")

    val americano: Beverage = new Beverage(
      "B002",
      "Americano",
      4.99,
      "Espresso shots topped with hot water create a light layer of crema.",
      30,
      new Image(getClass.getResourceAsStream("/images/americano.jpg")),
      "Active")

    val moccaccino: Beverage = new Beverage(
      "B002",
      "Moccaccino",
      9.99,
      "A delicious and refreshing moccaccino",
      10,
      new Image(getClass.getResourceAsStream("/images/moccaccino.png")),
      "Active")

    val iceLatte: Beverage = new Beverage(
      "B003",
      "Ice Latte",
      7.99,
      "A delicious and refreshing ice latte",
      10,
      new Image(getClass.getResourceAsStream("/images/iced-latte.png")),
      "Active")

    val oatmilkMocha: Beverage = new Beverage(
      "B004",
      "Oatmilk Mocha",
      12.99,
      "A delicious and refreshing oatmilk mocha",
      10,
      new Image(getClass.getResourceAsStream("/images/Oatmilk-Mocha-Flash-Brew.png")),
      "Active")


  // List of all predefined beverages
  val beverages: List[Beverage] = List(americano, moccaccino, iceLatte, oatmilkMocha, mixBerrySmoothie, mangoSmoothie)

  // Method to get all beverages
  def getAllBeverages: List[Beverage] = {
    beverages
  }


}