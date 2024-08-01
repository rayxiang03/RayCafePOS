package my.ray.app.model

import scalafx.beans.property.{ObjectProperty, StringProperty}
import scalafx.scene.image.Image

class Dessert(
               _id: Option[Long],
               _name: String,
               _price: Double,
               _description: String,
               _stock: Int,
               _imagePath: Image,
               _status: String
             ) extends Product(_id, _name, _price, _description, _stock, _imagePath, _status) with Consumable {

  var productName = new StringProperty(_name)
  var productPrice = new StringProperty(_price.toString)
  var productImage: ObjectProperty[Image] = ObjectProperty(_imagePath)
  var productDescription = new StringProperty(_description)

}

  object Dessert {
    val matchamisu: Dessert = new Dessert(
      Some(1L),
      "Matchamisu",
      26.50,
      "Vanilla Mascarpone Cream Dusted with Matcha Powder",
      10,
      new Image(getClass.getResourceAsStream("/images/matchamisu.png")),
      "Active")

    val chococherrytart: Dessert = new Dessert(
      Some(2L),
      "Choco Cherry Tart",
      24.50,
      "Chocolate Tart with Cherry Compote",
      10,
      new Image(getClass.getResourceAsStream("/images/chococherrytart.png")),
      "Active")

    val rockmelonbingsu: Dessert = new Dessert(
      Some(3L),
      "Rockmelon Bingsu",
      22.50,
      "Korean Shaved Ice Dessert with Rockmelon",
      10,
      new Image(getClass.getResourceAsStream("/images/rockmelonbingsu.png")),
      "Active")

    val desserts: List[Dessert] = List(matchamisu, chococherrytart, rockmelonbingsu)

    def getAllDesserts: List[Dessert] = {
      desserts
    }
}


