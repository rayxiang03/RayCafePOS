package my.ray.app.view

import javafx.fxml.FXML
import my.ray.app.MainApp
import my.ray.app.util.SessionManager
import scalafx.application.Platform
import scalafxml.core.macros.sfxml
import scalafx.scene.control.ButtonBar
import scalafx.scene.control.Button


@sfxml
class NavigationController {


  def getDashboard() = {
    MainApp.showDashboard()

  }

  def getOrder() = {
    MainApp.showOrderPage()
  }

  def logout(): Unit = {
    SessionManager.endSession()
    Platform.runLater(() => {
      MainApp.showLogin()
    })
  }
}
