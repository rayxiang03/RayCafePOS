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
  def idProperty: StringProperty = new StringProperty(id)
  def nameProperty: StringProperty = new StringProperty(name)
  def priceProperty: DoubleProperty = new DoubleProperty(this, "price", price)
  def descriptionProperty: StringProperty = new StringProperty(description)
  def stockProperty: IntegerProperty = new IntegerProperty(this, "stock", stock)
  def imageProperty: ObjectProperty[Image] = new ObjectProperty(this, "imagePath", imagePath)
}

object Product {
  def apply(
             id: String,
             name: String,
             price: Double,
             description: String,
             stock: Int,
             imagePath: Image,
             status: String
           ): Product = new Product(id, name, price, description, stock, imagePath, status) {}
}
