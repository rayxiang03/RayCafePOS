package my.ray.app.model

import scalafx.beans.property.{DoubleProperty, IntegerProperty, StringProperty}
import scalikejdbc._

import scala.util.{Failure, Success, Try}
import my.ray.app.util.Database
import scalafx.scene.image.Image

abstract class Product(
                        val id: Option[Long],
                        val name: String,
                        val price: Double,
                        val description: String,
                        val stock: Int,
                        val imagePath: Image,
                        val status: String
                      ) {

}
