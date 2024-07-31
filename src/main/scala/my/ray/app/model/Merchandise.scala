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
