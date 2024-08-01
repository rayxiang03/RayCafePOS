package my.ray.app.view

import my.ray.app.MainApp

import scalafxml.core.macros.sfxml

@sfxml
class OrderController {

    def getBeverage(): Unit = {
        MainApp.showOrderCategory("Beverage")
    }

    def getDesserts(): Unit = {
        MainApp.showOrderCategory("Dessert")
    }

    def getMainCourse(): Unit = {
        MainApp.showOrderCategory("MainCourse")
    }

    def getSalad(): Unit = {
        MainApp.showOrderCategory("Salad")
    }

    def getMerchandise(): Unit = {
        MainApp.showOrderCategory("Merchandise")
    }

}
