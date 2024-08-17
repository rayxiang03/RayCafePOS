package my.ray.app

import my.ray.app.util.Database
import scalafx.application.{JFXApp, Platform}
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.image.Image
import scalafx.Includes._
import scalafxml.core.{FXMLLoader, NoDependencyResolver}
import javafx.{scene => jfxs}
import my.ray.app.model.{Beverage, Dessert, MainCourse, Merchandise, OrderTransaction, Product, Salad, Table}
import my.ray.app.view.{LoginController, PaymentController, ProductCardController, ProductController, ReceiptController, TableSelectionController}
import scalafx.animation.{PauseTransition, ScaleTransition}
import scalafx.collections.ObservableBuffer
import scalafx.scene.layout.{BorderPane, StackPane}
import scalafx.stage.{Modality, Stage, StageStyle}
import scalafx.util.Duration

object MainApp extends JFXApp {

  Database.setupDB()

  // Observable buffer
  val productData = new ObservableBuffer[Product]()
  val currentOrderItems = new ObservableBuffer[(Product, Int)]()
  val tableDetails = new ObservableBuffer[Table]()
  tableDetails ++= Table.getAllTables
  val availableTable = new ObservableBuffer[Table]()
  availableTable ++= Table.getAvailableTables

  // Preferences & Behaviors tracking
  var isTakeAwayChecked: Boolean = false
  var productCardMode: String = _

  //Load RootLayout.fxml
  val rootResource = getClass.getResource("view/RootLayout.fxml")
  val loader = new FXMLLoader(rootResource, NoDependencyResolver)
  loader.load()

  //Transform path of RootLayout.fxml to URI for resource location.
  val roots: BorderPane = loader.getRoot[jfxs.layout.BorderPane]()

  //Popup Stage for Table Selection
  var popupStage: Stage = _


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
      case "Beverages" => productData ++= Beverage.getAllBeverages
      case "Desserts" => productData ++= Dessert.getAllDesserts
      case "Main Courses" => productData ++= MainCourse.getAllMainCourses
      case "Salads" => productData ++= Salad.getAllSalads
      case "Merchandises" => productData ++= Merchandise.getMerchandiseData
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


  def showProductCard(product: Product, mode: String, quantity: Int = 1): Unit = {
    productCardMode = mode
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

    // Set the initial value of the spinner if in update mode
    if (mode == "update") {
      productCardController.tempQty = quantity
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
      currentOrderItems.clear()
      val pause = new PauseTransition(Duration(3000))
      pause.setOnFinished(_ => controller.clearLogoutMessage())
      pause.play()

    } else {
      controller.clearLogoutMessage()
    }
  }

  //Default is from Navigation, but can be from Order as well.
  def showTablePage(pageSource: String = "from_Navigation", onTableSelected: Table => Unit = _ => ()): Unit = {
    val resource = getClass.getResource("view/TableSelection.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load();
    val tableSelectionPane = loader.getRoot[jfxs.layout.AnchorPane]
    val fullTablePane = tableSelectionPane.lookup("#fullTablePane").asInstanceOf[jfxs.layout.Pane]

    val tableController = loader.getController[TableSelectionController#Controller]
    tableController.pageSource = pageSource
    tableController.handleModifyAdd // Set the button text based on the page source
    tableController.onTableSelected = onTableSelected // Pass the callback function

    pageSource match {
      case "from_Navigation" =>
        this.roots.setCenter(tableSelectionPane)

      case "from_Order" =>
        val newRootPane = new jfxs.layout.AnchorPane()
        val fullTablePaneCopy = new jfxs.layout.Pane()
        fullTablePaneCopy.getChildren.addAll(fullTablePane.getChildren)
        newRootPane.getChildren.add(fullTablePaneCopy)

        // Create a new Stage (popup) for table selection
         popupStage = new Stage() {
          initModality(Modality.ApplicationModal)
          initStyle(StageStyle.UTILITY)
          title = "Select a Table for Order"
          scene = new Scene(newRootPane) {
            minWidth = 920
            minHeight = 530
            maxWidth = 920
            maxHeight = 530
          }
        }
        popupStage.showAndWait()
    }
  }

  def refreshTableDetails(): Unit = {
    tableDetails.clear()
    tableDetails ++= Table.getAllTables
    availableTable.clear()
    availableTable ++= Table.getAvailableTables
  }


  def showPaymentPage(currentOrderItems: List[(Product, Int)], subTotal: Double, serviceCharge: Double, sst: Double, total: Double, tableNo: Option[String] = None): Unit = {
    val resource = getClass.getResource("view/Payment.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val paymentPane = loader.getRoot[jfxs.layout.AnchorPane]

    val paymentStage = new Stage() {
      initModality(Modality.ApplicationModal)
      initOwner(stage)
      initStyle(StageStyle.TRANSPARENT)
      scene = new Scene(paymentPane) {
        fill = null
      }
    }

    val controller = loader.getController[PaymentController#Controller]
    controller.setOrderItems(currentOrderItems)
    controller.setAmounts(subTotal, serviceCharge, sst, total)
    controller.paymentStage = paymentStage
    controller.setTableNo(tableNo)

    paymentStage.showAndWait()

  }


  def showReceipt(orderID: String): Unit = {
    val resource = getClass.getResource("view/Receipt.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val roots = loader.getRoot[jfxs.layout.AnchorPane]

    val orderOption = OrderTransaction.findById(orderID)
    orderOption match {
      case Some(order) =>
        val controller = loader.getController[ReceiptController#Controller]
        controller.setOrderTransaction(order)

        // Create a new Stage for the popup
        val receiptStage = new Stage() {
          initModality(Modality.ApplicationModal)
          initOwner(stage)
          initStyle(StageStyle.UTILITY)
          title = "Receipt"
          scene = new Scene(roots)
        }

        // Show the popup
        receiptStage.showAndWait()

      case None =>
        // Handle the case when the order is not found
        println(s"Order with ID $orderID not found.")
    }
  }

  def showOrderHistoryPage(): Unit = {
    val resource = getClass.getResource("view/OrderHistory.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val roots = loader.getRoot[jfxs.layout.AnchorPane]
    this.roots.setCenter(roots)
  }

  showLogin()
}
