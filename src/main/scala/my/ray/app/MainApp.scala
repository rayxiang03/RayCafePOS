package my.ray.app

import my.ray.app.util.Database
import scalafx.application.{JFXApp, Platform}
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.image.Image
import scalafx.Includes._
import scalafxml.core.{FXMLLoader, NoDependencyResolver}
import javafx.{scene => jfxs}
import my.ray.app.model.{Beverage, Dessert, MainCourse, Merchandise, Product, Salad}
import my.ray.app.view.{LoginController, ProductCardController, ProductController}
import scalafx.animation.{PauseTransition, ScaleTransition}
import scalafx.collections.ObservableBuffer
import scalafx.scene.layout.{BorderPane, StackPane}
import scalafx.stage.{Modality, Stage, StageStyle}
import scalafx.util.Duration

object MainApp extends JFXApp {

  Database.setupDB()

  // Observable buffer for products
  val productData = new ObservableBuffer[Product]()


  //Load RootLayout.fxml
  val rootResource = getClass.getResource("view/RootLayout.fxml")
  val loader = new FXMLLoader(rootResource, NoDependencyResolver)
  loader.load()

  //Transform path of RootLayout.fxml to URI for resource location.
  val roots: BorderPane = loader.getRoot[jfxs.layout.BorderPane]()


  stage = new PrimaryStage {
    title = "Ray's Cafe"
    minWidth = 1200
    maxWidth = 1200
    minHeight = 650
    maxHeight = 650

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


  def showOrderPage() = {
    val resource = getClass.getResource("view/Order.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load();
    val roots = loader.getRoot[jfxs.layout.AnchorPane]
    this.roots.setCenter(roots)
  }


  def showOrderCategory(category: String) = {
    productData.clear() //avoid duplicate looping data

    category match {
      case "Beverages" =>
        productData ++= Beverage.getAllBeverages

      case "Desserts" =>
        productData ++= Dessert.getAllDesserts

      case "Main Courses" =>
        productData ++= MainCourse.getAllMainCourses

      case "Salads" =>
        productData ++= Salad.getAllSalads

      case "Merchandises" =>
        productData ++= Merchandise.getMerchandiseData

      case _ =>
    }

    val resource = getClass.getResource("view/ProductCategory.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load();
    val roots = loader.getRoot[jfxs.layout.AnchorPane]
    this.roots.setCenter(roots)

    val controller = loader.getController[ProductController#Controller]
    controller.updateTable(category)
  }


  def showProductCard(product: Product): Unit = {
    val resource = getClass.getResource("view/ProductCard.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val roots2 = loader.getRoot[jfxs.Parent]
    val productCardController = loader.getController[ProductCardController#Controller]


    // Create a transparent stage to avoid white screen
    val dialog = new Stage() {
      initModality(Modality.ApplicationModal)
      initOwner(stage)
      initStyle(StageStyle.TRANSPARENT) // Transparent stage style
      scene = new Scene {
        root = new StackPane() {
          children = roots2
        }
        stylesheets += getClass.getResource("view/Style.css").toString
      }
    }

    // Set initial scale to 0 for the animation
    roots2.setScaleX(0)
    roots2.setScaleY(0)

    // Define the scale transition
    val scaleTransition = new ScaleTransition(Duration(300), roots2) {
      fromX = 0
      fromY = 0
      toX = 1
      toY = 1
    }

    productCardController.dialogStage = dialog
    productCardController.product = product

    // Show the dialog with a transparent background
    dialog.show()

    // Use Platform.runLater to ensure the animation starts after the dialog is shown
    Platform.runLater {
      scaleTransition.play()
    }
  }

  def showLogin(logoutSuccessful: Boolean = false): Unit = {
    val resource = getClass.getResource("view/Login.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load();
    val Loginroots = loader.getRoot[jfxs.layout.AnchorPane]
    this.roots.setCenter(Loginroots)

    val controller = loader.getController[LoginController#Controller]
    if (logoutSuccessful) {
      controller.displayLogoutMessage()

      val pause = new PauseTransition(Duration(3000))
      pause.setOnFinished(_ => controller.clearLogoutMessage())
      pause.play()

    } else {
      controller.clearLogoutMessage()
    }
  }

  //First Page
  showLogin()

}