package my.ray.app.model

import scalafx.scene.image.Image

class MainCourse(
                  _id: Option[Long],
                  _name: String,
                  _price: Double,
                  _description: String,
                  _stock: Int,
                  _imagePath: Image,
                  _status: String
                ) extends Product(_id, _name, _price, _description, _stock, _imagePath, _status) with Consumable {

}

object MainCourse {
  val chickenParmigiana: MainCourse = new MainCourse(
    Some(1L),
    "Chicken Parmigiana",
    26.50,
    "Crispy Chicken Breast with Tomato Sauce and Melted Cheese",
    10,
    new Image(getClass.getResourceAsStream("/images/chicken-parmigiana.jpg")),
    "Active")

  val curryMee: MainCourse = new MainCourse(
    Some(2L),
    "Curry Mee",
    24.50,
    "Curry Noodles with Fish Cake, Fishball and Prawn",
    10,
    new Image(getClass.getResourceAsStream("/images/curry-mee.jpg")),
    "Active")

  val carbonaraBeacon: MainCourse = new MainCourse(
    Some(3L),
    "Carbonara Bacon",
    22.50,
    "Creamy Carbonara with Bacon and Mushroom",
    16,
    new Image(getClass.getResourceAsStream("/images/carbonara-beacon.jpg")),
    "Active")

  val fishAndChips: MainCourse = new MainCourse(
    Some(4L),
    "Fish and Chips",
    22.50,
    "Beer Battered Fish with Fries and Tartar Sauce",
    30,
    new Image(getClass.getResourceAsStream("/images/fish-and-chips.jpg")),
    "Active")

  val kidMacAndCheese: MainCourse = new MainCourse(
    Some(5L),
    "Kid Mac and Cheese",
    9.50,
    "Macaroni with Cheese Sauce and lots of vegetables (kids confirm like it! >_<)",
    20,
    new Image(getClass.getResourceAsStream("/images/kid-mac-and-cheese.jpg")),
    "Active")

  val seafoodPasta: MainCourse = new MainCourse(
    Some(6L),
    "Seafood Pasta",
    26.50,
    "Spaghetti with Prawns, Squid, and Mussels in Tomato Sauce",
    10,
    new Image(getClass.getResourceAsStream("/images/seafood-pasta.jpg")),
    "Active")

  val friedRiceWithSatay: MainCourse = new MainCourse(
    Some(7L),
    "Fried Rice with Satay",
    22.50,
    "Fried Rice with Chicken Satay and Peanut Sauce",
    10,
    new Image(getClass.getResourceAsStream("/images/fried-rice-with-satay.jpg")),
    "Active")

  val mainCourses: List[MainCourse] = List(chickenParmigiana, curryMee, carbonaraBeacon, fishAndChips, kidMacAndCheese, seafoodPasta, friedRiceWithSatay)

  def getAllMainCourses: List[MainCourse] = {
    mainCourses
  }
}


