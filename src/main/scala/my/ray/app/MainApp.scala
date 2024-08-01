package my.ray.app

import my.ray.app.util.Database
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.image.Image
import scalafx.Includes._
import scalafxml.core.{FXMLLoader, FXMLView, NoDependencyResolver}
import javafx.{scene => jfxs}
import my.ray.app.model.{Beverage, Dessert, MainCourse, Merchandise, Product, Salad}
import my.ray.app.view.{ProductCardController}
import scalafx.beans.property.{ObjectProperty, StringProperty}
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.{TableColumn, TableView}
import scalafx.stage.{Modality, Stage, StageStyle}



object MainApp extends JFXApp {

  Database.setupDB()


  // Observable buffer for products
  val productData = new ObservableBuffer[Product]()



  //Load RootLayout.fxml
  val rootResource = getClass.getResource("view/RootLayout.fxml")
  val loader = new FXMLLoader(rootResource, NoDependencyResolver)
  loader.load()

  //Transform path of RootLayout.fxml to URI for resource location.
  val roots = loader.getRoot[jfxs.layout.BorderPane]

  stage = new PrimaryStage {
    title = "Ray's Cafe"
    minWidth = 1200
    maxWidth = 1200
    minHeight = 650
    maxWidth = 650

    icons += new Image(getClass.getResourceAsStream("/images/raycafe_logo.png"))

    scene = new Scene {
      stylesheets += getClass.getResource("view/style.css").toString
      root = roots

    }
  }


  def showDashboard(): Unit = {
    val resource = getClass.getResource("view/Dashboard.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load();
    val roots = loader.getRoot[jfxs.layout.AnchorPane]
    this.roots.setCenter(roots)
  }


  def showOrderPage()={
    val resource = getClass.getResource("view/Order.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load();
    val roots = loader.getRoot[jfxs.layout.AnchorPane]
    this.roots.setCenter(roots)
  }


  def showOrderCategory(category: String)= {

    productData.clear() //avoid duplicate looping data

    category match {
      case "Beverage" =>
        productData ++= Beverage.getAllBeverages

      case "Dessert" =>
        productData ++= Dessert.getAllDesserts

    }

    val resource = getClass.getResource("view/ProductCategory.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load();
    val roots = loader.getRoot[jfxs.layout.AnchorPane]
    this.roots.setCenter(roots)
  }


  def showProductCard(product: Product): Unit = {
    val resource = getClass.getResourceAsStream("view/ProductCard.fxml")
    val loader = new FXMLLoader(null, NoDependencyResolver)
    loader.load(resource);
    val roots2  = loader.getRoot[jfxs.Parent]
    val control = loader.getController[ProductCardController#Controller]

    val dialog = new Stage() {
      initModality(Modality.ApplicationModal)
      initOwner(stage)
      initStyle(StageStyle.Undecorated)
      scene = new Scene {
        stylesheets += getClass.getResource("view/Style.css").toString
        root = roots2
      }
    }
    control.dialogStage = dialog
    control.product = product
    dialog.showAndWait()
    control.okClicked
  }



  showDashboard()
}