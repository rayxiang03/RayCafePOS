package my.ray.app.view

import my.ray.app.MainApp
import my.ray.app.model.Beverage
import scalafx.scene.control.{TableCell, TableColumn, TableView}
import scalafx.scene.image.{Image, ImageView}
import scalafxml.core.macros.sfxml
import scalafx.Includes._
import scalafx.scene.Scene
import scalafx.stage.{Popup, Stage}
import scalafxml.core.{FXMLLoader, NoDependencyResolver}
import scalafx.scene.layout.{Pane, VBox}
import javafx.{scene => jfxs}


@sfxml
class BeverageController(
                          private val beverageTable: TableView[Beverage],
                          private val prodPicture: TableColumn[Beverage, Image],
                          private val prodName: TableColumn[Beverage, String],
                          private val prodPrice: TableColumn[Beverage, String]
                        ) {

  beverageTable.items = MainApp.beverageData

  // Set cell value factories
  prodName.cellValueFactory = _.value.productName
//  prodPrice.cellValueFactory = _.value.productPrice
  prodPicture.cellValueFactory = _.value.productImage

  // Set cell value factories
  //  prodPrice.cellValueFactory = _.value.productPrice

  // Set cell factory for the image column using ScalaFX
  prodPicture.cellFactory = { _ =>
    new TableCell[Beverage, Image] {
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

//  private def showProductPopup(beverage: Option[Beverage]): Unit = {
//    beverage match {
//      case Some(beverage) =>
//
//      case None =>
////        MainApp.showProductPopup(null)
//    }
//  }
//
//  showProductPopup(None)
//
//
//    beverageTable.selectionModel().selectedItem.onChange { (_, _, newValue) =>
//      showProductPopup(Option(newValue))
//    }


  private def showProductCard(beverage: Beverage): Unit = {
    MainApp.showProductCard(beverage)  // Call the showProductCard method of MainApp
  }

  // Listen for selection changes and show product card
  beverageTable.selectionModel().selectedItem.onChange { (_, _, newValue) =>
    newValue match {
      case null => // No action needed
      case beverage: Beverage => showProductCard(beverage)
    }
  }
}
