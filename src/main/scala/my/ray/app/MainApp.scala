package my.ray.app

import my.ray.app.util.Database

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.image.Image
import scalafx.Includes._
import scalafxml.core.{FXMLLoader, FXMLView, NoDependencyResolver}
import javafx.{scene => jfxs}



object MainApp extends JFXApp {

  Database.setupDB()

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

  showDashboard()
}