package my.ray.app.view

import my.ray.app.MainApp

import scalafxml.core.macros.sfxml

@sfxml
class OrderController {

    def getBeverage(): Unit = {
        MainApp.showOrderCategory("Beverages")
    }

    def getDesserts(): Unit = {
        MainApp.showOrderCategory("Desserts")
    }

    def getMainCourse(): Unit = {
        MainApp.showOrderCategory("Main Courses")
    }

    def getSalad(): Unit = {
        MainApp.showOrderCategory("Salads")
    }

    def getMerchandise(): Unit = {
        MainApp.showOrderCategory("Merchandises")
    }

}
