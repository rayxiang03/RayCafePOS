package my.ray.app.view

import my.ray.app.MainApp
import my.ray.app.model.Product
import scalafx.scene.control.{TableCell, TableColumn, TableView}
import scalafx.scene.image.{Image, ImageView}
import scalafxml.core.macros.sfxml
import scalafx.Includes._
import scalafx.scene.Scene
import scalafx.scene.layout.{HBox, StackPane}
import scalafx.stage.{Stage, StageStyle}


@sfxml
class ProductController(
                          private val orderTable: TableView[Product],
                          private val prodPicture: TableColumn[Product, Image],
                          private val prodName: TableColumn[Product, String],
                          private val prodPrice: TableColumn[Product, String],
                          private val prodCategory: TableColumn[Product, String],
                          private val banner: HBox,
                          private val bannerImage1: ImageView,
                        ) {

  // ObservableBuffer for all products
  private val allProducts = MainApp.productData

  // Set initial items
  orderTable.items = allProducts


  // Set cell value factories
  prodName.cellValueFactory = _.value.nameProperty
  prodPicture.cellValueFactory = _.value.imageProperty
  //  prodPrice.cellValueFactory = _.value.priceProperty

  // Set cell factory for the image column
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


  private def showProductCard(product: Product, mode: String): Unit = {
    MainApp.showProductCard(product, mode) // Call the showProductCard method of MainApp
  }

  // Add mouse click event handler to detect row clicks
  orderTable.onMouseClicked = _ => {
    val selectedProduct = orderTable.selectionModel().selectedItem.value
    if (selectedProduct != null) {
      showProductCard(selectedProduct, "add")
    }
  }

  def updateTable(category: String): Unit = {
    prodPicture.text = category;
  }

  bannerImage1.image = new Image(getClass.getResourceAsStream("/images/banner1.png"))
  bannerImage1.onMouseClicked = _ => {
    // Load the detail image when the banner is clicked
    val detailImage = new Image(getClass.getResourceAsStream("/images/banner1_details.png"))

    // Show the detail image in a popup
    showEnlargedImage(detailImage)
  }

  private def showEnlargedImage(image: Image): Unit = {
    val enlargedImageView = new ImageView(image) {
      fitWidth = 600
      fitHeight = 600
      preserveRatio = true
    }

    val pane = new StackPane() {
      children = enlargedImageView // Wrap ImageView in ScalaFX StackPane
    }

    val popupStage = new Stage() {
      title = "Latest News Banner"
      icons.add(new Image(getClass.getResourceAsStream("/images/raycafe_logo.png"))) // Set the icon for the stage
      scene = new Scene(pane, 600, 610)
    }

    popupStage.show()
  }
}