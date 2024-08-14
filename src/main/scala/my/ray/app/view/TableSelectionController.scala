package my.ray.app.view

import my.ray.app.MainApp
import my.ray.app.model.Table
import scalafx.scene.control.{Button, Label}
import scalafx.scene.layout.Pane
import scalafx.scene.shape.{Circle, Rectangle, Shape}
import scalafx.scene.paint.Color
import scalafxml.core.macros.sfxml
import scala.collection.convert.ImplicitConversions.`collection AsScalaIterable`

@sfxml
class TableSelectionController(
                                private val Table_A01: Rectangle,
                                private val Table_A02: Rectangle,
                                private val Table_A03: Circle,
                                private val Table_A04: Circle,
                                private val Table_A05: Circle,
                                private val Table_A06: Circle,
                                private val Table_A07: Rectangle,
                                private val Table_A08: Rectangle,
                                private val tableNoLabel: Label,
                                private val tablePaxLabel: Label,
                                private val tableAvailabilityLabel: Label,
                                private val tableDetailsLabel: Label,
                                private val modifyButton: Button,
                                private val mainPane: Pane
                              ) {

  // ObservableBuffer for all tables
  private val tableDetails = MainApp.tableDetails

  // Constants for table colors
  private val availableColor: Color = Color.rgb(165, 219, 153) // Light green
  private val inUseColor: Color = Color.rgb(255, 190, 190) // Light red
  private val selectedColor: Color = Color.rgb(248, 247, 147) // Light yellow

  // Variable to keep track of the currently selected table
  private var selectedTable: Option[Table] = None

  // Store the original content of mainPane
  private val originalContent = mainPane.children.toList

  // Hide the modify button initially
  modifyButton.visible = false

  private def getShapeById[T <: Shape](tableId: String): Option[T] = {
    val shape = tableId match {
      case "A01" => Table_A01
      case "A02" => Table_A02
      case "A03" => Table_A03
      case "A04" => Table_A04
      case "A05" => Table_A05
      case "A06" => Table_A06
      case "A07" => Table_A07
      case "A08" => Table_A08
      case _ => null
    }
    Option(shape).map(_.asInstanceOf[T])
  }

  private def resetTableColors(): Unit = {
    tableDetails.foreach { table =>
      getShapeById[Shape](table.tableId.value).foreach { shape =>
        shape.setFill(if (table.availability.value) availableColor else inUseColor)
      }
    }
  }

  private def updateLabels(table: Table): Unit = {
    tableNoLabel.text = table.tableId.value
    tablePaxLabel.text = table.pax.value.toString
    tableAvailabilityLabel.text = if (table.availability.value) "Available" else "In Use"
    tableDetailsLabel.text = table.details.value
  }

  private def handleTableClick(shape: Shape, table: Table): Unit = {
      resetTableColors()

    if (selectedTable.contains(table)) {
      // Deselect the table
      clearLabels()
      selectedTable = None
      modifyButton.visible = false

    } else {
      // Select the table
      shape.setFill(selectedColor)
      updateLabels(table)
      selectedTable = Some(table)
      modifyButton.visible = true
      restoreOriginalContent()
    }
  }

  private def clearLabels(): Unit = {
    Seq(tableNoLabel, tablePaxLabel, tableAvailabilityLabel, tableDetailsLabel).foreach(_.text = "")
    mainPane.children.setAll(new Label("Please select a table!"))
  }

  private def restoreOriginalContent(): Unit = {
    mainPane.children.clear()
    mainPane.children.addAll(originalContent: _*)
  }

  tableDetails.foreach { table =>
    getShapeById[Shape](table.tableId.value).foreach { shape =>
      shape.setFill(if (table.availability.value) availableColor else inUseColor)
      shape.onMouseClicked = _ => handleTableClick(shape, table)
    }
  }

  def handleModify(): Unit = {
    selectedTable.foreach { table =>
      println(s"Modify table: ${table.tableId.value}")
    }
  }
  clearLabels()
}