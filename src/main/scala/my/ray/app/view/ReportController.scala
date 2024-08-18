package my.ray.app.view

import my.ray.app.model.OrderTransaction
import scalafx.scene.chart.{BarChart, LineChart, PieChart, XYChart}
import scalafx.scene.control.DatePicker
import scalafxml.core.macros.sfxml

import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

trait ChartData[X, Y] {
  def getData: Map[X, Y]
}

class SalesData(startDate: LocalDate, endDate: LocalDate) extends ChartData[String, Number] {
  override def getData: Map[String, Number] = {
    OrderTransaction.getSalesDataForDateRange(startDate, endDate).map {
      case (day, sales) => day.toString -> sales.asInstanceOf[Number]
    }
  }
}

class TopSalesPieData(startDate: LocalDate, endDate: LocalDate) extends ChartData[String, Number] {
  override def getData: Map[String, Number] = {
    OrderTransaction.getTop10MostSoldItemsForDateRange(startDate, endDate).map {
      case (productName, quantity) => productName -> quantity.asInstanceOf[Number]
    }
  }
}

@sfxml
class ReportController(
                        private var salesByMonthChart: BarChart[String, Number],
                        private var topSalesPieChart: PieChart,
                        private var fromDateInput: DatePicker,
                        private var toDateInput: DatePicker,
                        private var salesTrendChart: LineChart[String, Number],
                      ) {

  fromDateInput.setOnAction(_ => updateCharts())
  toDateInput.setOnAction(_ => updateCharts())

  private def updateCharts(): Unit = {
    val startDate = fromDateInput.getValue
    val endDate = toDateInput.getValue

    if (startDate != null && endDate != null && !startDate.isAfter(endDate)) {
      populateBarChart(startDate, endDate)
      populateTopSalesPieChart(startDate, endDate)
      populateSalesTrendChart(startDate, endDate)
    }
  }

  private def populateBarChart(startDate: LocalDate, endDate: LocalDate): Unit = {
    salesByMonthChart.getData.clear()
    salesByMonthChart.setTitle(s"Daily Sales Summary (${startDate} to ${endDate})")

    val salesData = new SalesData(startDate, endDate)
    val totalSales = salesData.getData.values.map(_.doubleValue()).sum
    val series = new XYChart.Series[String, Number]()
    series.setName(s"Total Sales: RM$totalSales")

    salesData.getData.foreach { case (day, sales) =>
      series.getData.add(XYChart.Data(day, sales))
    }

    populateChart(salesByMonthChart, salesData)
    salesByMonthChart.getData.setAll(series)

  }

  private def populateTopSalesPieChart(startDate: LocalDate, endDate: LocalDate): Unit = {
    topSalesPieChart.getData.clear()
    topSalesPieChart.setTitle(s"Top 8 Most Popular Products (${startDate} to ${endDate})")
    val topSalesData = new TopSalesPieData(startDate, endDate)
    populatePieChart(topSalesPieChart, topSalesData)
  }

  private def populateSalesTrendChart(startDate: LocalDate, endDate: LocalDate): Unit = {
    salesTrendChart.getData.clear()
    salesTrendChart.setTitle(s"Sales Trend (${startDate} to ${endDate})")

    val salesData = new SalesData(startDate, endDate)
    val totalDays = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1
    val series = new XYChart.Series[String, Number]()
    series.setName(s"Total Day(s) Filtered: $totalDays")

    salesData.getData.foreach { case (day, sales) =>
      series.getData.add(XYChart.Data(day, sales))
    }

    salesTrendChart.getData.setAll(series)
  }

  private def populateChart[X, Y](chart: XYChart[X, Y], data: ChartData[X, Y]): Unit = {
    val series = new XYChart.Series[X, Y]()
    series.setName("Total Sales")

    data.getData.foreach { case (x, y) =>
      series.getData.add(XYChart.Data(x, y))
    }
    chart.setLegendVisible(true)
    chart.getData.setAll(series)
  }

  private def populatePieChart(chart: PieChart, data: ChartData[String, Number]): Unit = {
    data.getData.foreach { case (name, quantity) =>
      val pieData = PieChart.Data(s"$name (${quantity.intValue()})", quantity.doubleValue())
      chart.getData.add(pieData)
    }
    chart.setLegendVisible(false)
  }

  private def getCurrentMonthDateRange: (LocalDate, LocalDate) = {
    val startDate = LocalDate.now().`with`(TemporalAdjusters.firstDayOfMonth())
    val endDate = LocalDate.now().`with`(TemporalAdjusters.lastDayOfMonth())
    (startDate, endDate)
  }

  val (startDate, endDate) = getCurrentMonthDateRange
  populateBarChart(startDate, endDate)
  populateTopSalesPieChart(startDate, endDate)
  populateSalesTrendChart(startDate, endDate)
}


