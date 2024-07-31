package my.ray.app.view

import javafx.fxml.FXML
import my.ray.app.MainApp
import scalafxml.core.macros.sfxml
import scalafx.scene.control.ButtonBar
import scalafx.scene.control.Button


@sfxml
class NavigationController {


  def getDashboard()= {
    MainApp.showDashboard()

  }

  def getOrder()= {
    MainApp.showOrderPage()
  }

}
