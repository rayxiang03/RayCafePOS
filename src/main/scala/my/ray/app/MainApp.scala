package my.ray.app

import scalafx.application.JFXApp
import scalafx.scene.image.Image

object MainApp extends JFXApp {

  stage = new PrimaryStage{
    title = "RayCafe"
    icons += new Image(getClass.getResourceAsStream("images/raydeli_logo.png"))

  }
}