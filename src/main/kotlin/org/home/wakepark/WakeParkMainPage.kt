package org.home.wakepark

import org.openqa.selenium.NoSuchElementException
import org.openqa.selenium.firefox.FirefoxDriver
import java.time.LocalTime

class WakeParkMainPage {
/*    private var driver: FirefoxDriver

    init {
        System.setProperty("webdriver.gecko.driver", "geckodriver-v0.26.0-win64\\geckodriver.exe");
        driver = FirefoxDriver()
//        driver.manage().window().position = Point(-2000, 0)
    }

    fun open(url: String) {
        driver.get(url)
    }

    fun refresh(date: String): Boolean {
        driver.navigate().refresh()
        try {
            val freeSlot = driver.findElement(By.className("gridFree")
            freeSlot?.let {
                return true
            } ?: return false
        }catch (e: NoSuchElementException){
            println("${LocalTime.now()} Cant find any free slot for $date")
            return false
        }
    }*/
}