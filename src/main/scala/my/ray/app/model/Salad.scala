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

  // You can define methods specific to Dessert here
}