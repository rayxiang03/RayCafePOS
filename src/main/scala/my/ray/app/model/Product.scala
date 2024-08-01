package my.ray.app.model

import scalafx.beans.property.{ObjectProperty, StringProperty}
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

  // Define properties for binding
  def nameProperty: StringProperty = new StringProperty(name)
  def priceProperty: StringProperty = new StringProperty(price.toString)
  def imageProperty: ObjectProperty[Image] = ObjectProperty(imagePath)
  def descriptionProperty: StringProperty = new StringProperty(description)
}
