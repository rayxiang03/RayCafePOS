package my.ray.app.view

import my.ray.app.MainApp
import my.ray.app.model.Product
import scalafx.Includes.integer2IntegerBinding
import scalafx.collections.ObservableBuffer
import scalafx.application.Platform
import scalafx.beans.property.{DoubleProperty, IntegerProperty}
import scalafxml.core.macros.sfxml
import scalafx.scene.control.{TableColumn, TableView}

import scala.collection.convert.ImplicitConversions.`collection AsScalaIterable`


@sfxml
class OrderController
(
  private val currentOrderTable: TableView[(Product, Int)],
  private val productColumn: TableColumn[(Product, Int), String],
  private val quantityColumn: TableColumn[(Product, Int), Int],
  private val priceColumn: TableColumn[(Product, Int), Double]
) {



  // List to hold the current order items
  private val currentOrderItems: ObservableBuffer[(Product, Int)] = ObservableBuffer()

  def initialize(): Unit = {
    currentOrderTable.items = currentOrderItems
  }

  def getBeverage(): Unit = {
    MainApp.showOrderCategory("Beverages")
  }

  def getDesserts(): Unit = {
    MainApp.showOrderCategory("Desserts")
  }

  def getMainCourse(): Unit = {
    MainApp.showOrderCategory("Main Courses")
  }

  def getSalad(): Unit = {
    MainApp.showOrderCategory("Salads")
  }

  def getMerchandise(): Unit = {
    MainApp.showOrderCategory("Merchandises")
  }

  // Method to add a product to the order
  def addProductToOrder(product: Product, quantity: Int): Unit = {
    val existingItemIndex = currentOrderItems.indexWhere(_._1.id == product.id)
    if (existingItemIndex >= 0) {
      val (existingProduct, existingQuantity) = currentOrderItems(existingItemIndex)
      currentOrderItems.update(existingItemIndex, (existingProduct, existingQuantity + quantity))
    } else {
      currentOrderItems += ((product, quantity))
    }
    currentOrderTable.refresh() // Ensure the table is refreshed after each modification
  }

}
