package my.ray.app.model

import scalikejdbc._

import scala.util.{Failure, Try}
import my.ray.app.util.Database
import scalafx.scene.image.Image

class Salad(
             _id: Option[Long],
             _name: String,
             _price: Double,
             _description: String,
             _stock: Int,
             _imagePath: Image,
             _status: String
             ) extends Product(_id, _name, _price, _description, _stock, _imagePath, _status) with Consumable {

}

object Salad {
  val caesarSalad: Salad = new Salad(
    Some(6L),
    "Caesar Salad",
    12.50,
    "Romaine Lettuce, Croutons, Parmesan Cheese, Lemon Juice, Olive Oil, Egg, Worcestershire Sauce, Garlic, Black Pepper, Salt",
    10,
    new Image(getClass.getResourceAsStream("/images/caesar-salad.jpg")),
    "Active")

  val gardenSalad: Salad = new Salad(
    Some(7L),
    "Garden Salad",
    10.50,
    "Lettuce, Tomato, Cucumber, Carrot, Onion, Olive Oil, Lemon Juice, Salt, Black Pepper",
    10,
    new Image(getClass.getResourceAsStream("/images/garden-salad.jpg")),
    "Active")

    val pecanberrySalad: Salad = new Salad(
    Some(8L),
    "Pecanberry Salad",
    15.50,
    "380 cal. Fresh strawberries and blueberries, candied pecans and grilled chicken, served with Fat-Free Raspberry-Pecan Vinaigrette dressing.",
    10,
    new Image(getClass.getResourceAsStream("/images/pecanberry-salad.jpg")),
    "Active")

    val saladList = List(caesarSalad, gardenSalad, pecanberrySalad)

    def getAllSalads: List[Salad] = {
      saladList
    }

}