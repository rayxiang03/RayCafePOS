package my.ray.app.model

import scalafx.scene.image.Image

class Merchandise(
                   _id: Option[Long],
                   _name: String,
                   _price: Double,
                   _description: String,
                   _stock: Int,
                   _imagePath: Image,
                   _status: String
                 ) extends Product(_id, _name, _price, _description, _stock, _imagePath, _status) {

}


object Merchandise {
  val personalizeMug: Merchandise = new Merchandise(
    Some(1L),
    "Personalize Mug",
    79.90,
    "Personalize your own mug with your favorite name!",
    10,
    new Image(getClass.getResourceAsStream("/images/personalize-coffee-mug.png")),
    "Active")

  val notebookSet: Merchandise = new Merchandise(
    Some(2L),
    "Notebook Set",
    49.90,
    "Notebook set with  pencil",
    10,
    new Image(getClass.getResourceAsStream("/images/notebook-set.png")),
    "Active")

  val merchandiseData = List(personalizeMug, notebookSet)

  def getMerchandiseData: List[Merchandise] = {
    merchandiseData
  }
}