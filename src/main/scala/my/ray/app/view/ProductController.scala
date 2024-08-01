package my.ray.app.view

import my.ray.app.MainApp
import my.ray.app.model.Product
import scalafx.scene.control.{TableCell, TableColumn, TableView}
import scalafx.scene.image.{Image, ImageView}
import scalafxml.core.macros.sfxml
import scalafx.Includes._
import scalafx.scene.Scene
import scalafx.stage.{Popup, Stage}
import scalafxml.core.{FXMLLoader, NoDependencyResolver}
import scalafx.scene.layout.{Pane, VBox}
import javafx.{scene => jfxs}
import javafx.beans.value.{ChangeListener, ObservableValue}
import scalafx.collections.ObservableBuffer


@sfxml
class ProductController(
                          private val orderTable: TableView[Product],
                          private val prodPicture: TableColumn[Product, Image],
                          private val prodName: TableColumn[Product, String],
                          private val prodPrice: TableColumn[Product, String]
                        ) {

  // ObservableBuffer for all products
  private val allProducts = MainApp.productData

  // Set initial items
  orderTable.items = allProducts


  // Set cell value factories
  prodName.cellValueFactory = _.value.nameProperty
  prodPicture.cellValueFactory = _.value.imageProperty
//  prodPrice.cellValueFactory = _.value.priceProperty

  // Set cell factory for the image column using ScalaFX
  prodPicture.cellFactory = { _ =>
    new TableCell[Product, Image] {
      private val imageView = new ImageView()

      item.onChange { (_, _, newImage) =>
        if (newImage == null) {
          graphic = null
        } else {
          imageView.image = newImage
          imageView.fitHeight = 150
          imageView.fitWidth = 200
          imageView.preserveRatio = true
          graphic = imageView
        }
      }
    }
  }


  private def showProductCard(product: Product): Unit = {
    MainApp.showProductCard(product)  // Call the showProductCard method of MainApp
  }

  // Add mouse click event handler to detect row clicks
  orderTable.onMouseClicked = _ => {
    val selectedBeverage = orderTable.selectionModel().selectedItem.value
    if (selectedBeverage != null) {
      showProductCard(selectedBeverage)
    }
  }
}

