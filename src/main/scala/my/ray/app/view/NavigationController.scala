package my.ray.app.view

import javafx.fxml.FXML
import my.ray.app.MainApp
import my.ray.app.util.SessionManager
import scalafx.application.Platform
import scalafx.stage.Stage
import scalafxml.core.macros.sfxml


@sfxml
class NavigationController {


  def getDashboard() = {
    MainApp.showDashboard()

  }

  def getOrder() = {
    MainApp.showOrderPage()
  }

  def getOrderHistory(): Unit = {
    val loadingStage = MainApp.showLoadingPopup()
    Platform.runLater(() => {
      MainApp.showOrderHistoryPage()
      MainApp.hideLoadingPopup(loadingStage)
    })
  }

  def getTableManagement() = {
    val loadingStage = MainApp.showLoadingPopup()
    Platform.runLater(() => {
      MainApp.showTablePage()
      MainApp.hideLoadingPopup(loadingStage)
    })
  }

  def getReport() {
    MainApp.showReportPage()
  }

  def logout(): Unit = {
    SessionManager.endSession()
    Platform.runLater(() => {
      MainApp.showLogin(logoutSuccessful = true)
    })
  }
}
