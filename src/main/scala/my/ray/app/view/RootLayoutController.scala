package my.ray.app.view

import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import scalafxml.core.macros.sfxml

@sfxml
class RootLayoutController {
  def handleAbout(): Unit = {
    val alert = new Alert(AlertType.INFORMATION)
    alert.setTitle("About Ray Cafe")
    alert.setHeaderText("Welcome to Ray Cafe!")
    alert.setContentText(
      """Ray Cafe is a cozy place where you can enjoy a variety of delicious beverages and snacks.
        |Founded in 2024, our mission is to provide a relaxing environment for our customers to unwind and enjoy quality time with friends and family.
        |
        |Contact Information:
        |Email: contact@raycafe.com
        |Phone: +603-1100 8866
        |Address: Lot 10, Jalan PJS 1/1, Bandar Sunway, 47500 Subang Jaya, Selangor, Malaysia.
        |
        |Application Version: 1.0.1 (Beta)
        |Java Version: 8
        |Scala Version: 2.12.19
        |ScalaFX Version: 8.0.192-R14
        |Last Update Date: 20-Aug-2024
        |
        |Thank you for visiting Ray Cafe!""".stripMargin)
    alert.showAndWait()
  }
}
