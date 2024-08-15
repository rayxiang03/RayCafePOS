package my.ray.app.view

import javafx.event.ActionEvent
import my.ray.app.MainApp
import my.ray.app.model.{Product, Table}
import scalafx.beans.property.ObjectProperty
import scalafxml.core.macros.sfxml
import scalafx.scene.control.{Alert, CheckBox, MenuItem, SplitMenuButton, TableColumn, TableView}
import scalafx.scene.text.Text
import javafx.{scene => jfxs}
import scalafx.scene.layout.Pane

@sfxml
class OrderController(
                       private val currentOrderTable: TableView[(Product, Int)],
                       private val productColumn: TableColumn[(Product, Int), String],
                       private val quantityColumn: TableColumn[(Product, Int), Int],
                       private val priceColumn: TableColumn[(Product, Int), String],
                       private val subTotal: Text,
                       private val serviceCharge: Text,
                       private val sstCharge: Text,
                       private val total: Text,
                       private val isTakeAway: CheckBox,
                       private val tableSelectionPane: Pane,
                       private val chooseTableNo: SplitMenuButton
                     ) {

  // ObservableBuffer for current order items
  private val currentOrderItems = MainApp.currentOrderItems
  currentOrderTable.items = currentOrderItems

  private val takeAwayStatus = MainApp.isTakeAwayChecked
  tableSelectionPane.disable = takeAwayStatus
  isTakeAway.selected.value = takeAwayStatus

  // ObservableBuffer for all tables
  private val availableTable = MainApp.availableTable


  // Recalculate totals whenever the order items change or the checkbox is toggled
  currentOrderItems.onChange(updateTotals())
  isTakeAway.selected.onChange { (_, _, newValue) =>
    tableSelectionPane.disable = newValue
    updateTotals()
  }

  // Listener for row selection
  currentOrderTable.onMouseClicked = _ => {
    val selectedProduct = currentOrderTable.selectionModel().selectedItemProperty().getValue
    if (selectedProduct != null) {
      val product = selectedProduct._1
      val quantity = selectedProduct._2

      MainApp.showProductCard(product, "update", quantity)
    }
  }

  def handleCategoryChange(event: ActionEvent): Unit = {
    saveTakeawayStatus()
    val button = event.getSource.asInstanceOf[jfxs.control.Button]
    MainApp.showOrderCategory(button.getText)
  }

  // Save the current state of the takeaway checkbox to MainApp
  private def saveTakeawayStatus(): Unit = {
    MainApp.isTakeAwayChecked = isTakeAway.selected.value
  }

  // Initialize the TableView columns
  productColumn.cellValueFactory = cellData => ObjectProperty(cellData.value._1.name)
  quantityColumn.cellValueFactory = cellData => ObjectProperty(cellData.value._2)
  priceColumn.cellValueFactory = { cellData =>
    val totalPrice = cellData.value._1.price * cellData.value._2
    ObjectProperty(f"$totalPrice%.2f")
  }

  private def updateTotals(): Unit = {
    val subtotalValue = currentOrderItems.map { case (product, quantity) =>
      product.price * quantity
    }.sum

    val serviceChargeValue = if (isTakeAway.selected.value) 0 else subtotalValue * 0.1
    val sstValue = subtotalValue * 0.06
    val totalValue = subtotalValue + serviceChargeValue + sstValue

    subTotal.text = f"$subtotalValue%.2f"
    serviceCharge.text = f"$serviceChargeValue%.2f"
    sstCharge.text = f"$sstValue%.2f"
    total.text = f"$totalValue%.2f"
  }

  // Method to add a product to the order
  def addProductToOrder(product: Product, quantity: Int): Unit = {
    val existingItemIndex = currentOrderItems.indexWhere(_._1.id == product.id)
    if (existingItemIndex >= 0) {
      // Update quantity for existing product
      val (existingProduct, existingQuantity) = currentOrderItems(existingItemIndex)
      currentOrderItems.update(existingItemIndex, (existingProduct, existingQuantity + quantity))
    } else {
      // Add new product to the order
      currentOrderItems += ((product, quantity))
    }
  }

  def updateProductInOrder(product: Product, quantity: Int): Unit = {
    val existingItemIndex = currentOrderItems.indexWhere(_._1.id == product.id)
    if (existingItemIndex >= 0) {
      // Update quantity for existing product
      val (existingProduct, existingQuantity) = currentOrderItems(existingItemIndex)
      currentOrderItems.update(existingItemIndex, (existingProduct, quantity))
    } else {
      // Add new product to the order (should not reach here in update mode)
      currentOrderItems += ((product, quantity))
    }
  }

  def removeProductFromOrder(product: Product): Unit = {
    val existingItemIndex = currentOrderItems.indexWhere(_._1.id == product.id)
    if (existingItemIndex >= 0) {
      currentOrderItems.remove(existingItemIndex)
    }
  }

  def getProductQuantityInOrder(product: Product): Int = {
    currentOrderItems.find(_._1 == product).map(_._2).getOrElse(0)
  }

  private def populateTableMenu(): Unit = {
    chooseTableNo.items.clear()
    availableTable.foreach { table =>
      val tableMenuItem = new MenuItem(table.tableId.value)
      tableMenuItem.onAction = _ => {
        chooseTableNo.text = table.tableId.value
      }
      chooseTableNo.items.add(tableMenuItem)
    }
  }

  def handleTableSelection(): Unit = {
    MainApp.showTablePage("from_Order", table => chooseTableNo.text = table.tableId.value)
  }

  def proceedPayment(): Unit = {
    if (currentOrderTable.getItems.isEmpty) {
      val alert = new Alert(Alert.AlertType.Warning) {
        initOwner(MainApp.stage)
        title = "Empty Order"
        headerText = "No products in the order"
        contentText = "Please add products to the order before proceeding to payment."
      }
      alert.showAndWait()
      return
    }
    val subtotalValue = subTotal.text.value.toDouble
    val serviceChargeValue = serviceCharge.text.value.toDouble
    val sstValue = sstCharge.text.value.toDouble
    val totalValue = total.text.value.toDouble
    val currentOrderItemsList = currentOrderItems.toList

    MainApp.showPaymentPage(currentOrderItemsList, subtotalValue, serviceChargeValue, sstValue, totalValue)
  }

  updateTotals()
  populateTableMenu()
}
