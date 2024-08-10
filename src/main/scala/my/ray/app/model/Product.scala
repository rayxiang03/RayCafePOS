package my.ray.app.model

import scalafx.beans.property.{DoubleProperty, IntegerProperty, ObjectProperty, StringProperty}
import scalafx.scene.image.Image

abstract class Product(
                        val id: String,
                        val name: String,
                        val price: Double,
                        val description: String,
                        val stock: Int,
                        val imagePath: Image,
                        val status: String
                      ) {

  // Define properties for binding
  def idProperty: StringProperty = new StringProperty(this, "id", id)
  def nameProperty: StringProperty = new StringProperty(this, "name", name)
  def priceProperty: DoubleProperty = new DoubleProperty(this, "price", price)
  def descriptionProperty: StringProperty = new StringProperty(this, "description", description)
  def stockProperty: IntegerProperty = new IntegerProperty(this, "stock", stock)
  def imageProperty: ObjectProperty[Image] = new ObjectProperty(this, "imagePath", imagePath)
}

