package org.home.lazada

import org.home.util.alerts.Sound.playSound
import org.openqa.selenium.By
import org.openqa.selenium.firefox.FirefoxDriver
import java.io.File
import java.time.LocalDateTime
import javax.sound.sampled.AudioSystem.getAudioInputStream
import javax.sound.sampled.AudioSystem.getClip
import javax.sound.sampled.Clip
import javax.sound.sampled.FloatControl


class LazadaMainPage {
    private var driver: FirefoxDriver

    init {
        System.setProperty("webdriver.gecko.driver", "geckodriver-v0.26.0-win64\\geckodriver.exe");
        driver = FirefoxDriver()
//        driver.manage().window().position = Point(-2000, 0)
    }

    fun openAndLogin(url: String, userID: String, password: String) {
        driver.get(url)
        driver.findElement(By.id("anonLogin")).click()
        driver.findElement(By.className("mod-login-input-loginName"))
            .findElement(By.ByXPath("//input[@type='text']"))
            .sendKeys(userID)

        driver.findElement(By.className("mod-login-input-password"))
            .findElement(By.ByXPath("//input[@type='password']"))
            .sendKeys(password)

        driver.findElement(By.className("next-btn-primary")).click()

    }


    private fun close() {
        driver.close()
    }

    fun clickOnCart() {
        driver.findElement(By.id(("topActionCartNumber"))).click()
        driver.findElement(By.className("checkout-shop-checkbox")).click()
        var clicked = false
        while (!clicked)
            try {
                driver.findElement(By.className("automation-checkout-order-total-button-button")).click()
                clicked = true
            } catch (e: Exception) {
                println("some exception trying again ... ")
            }
        driver.findElement(By.className("got-it-btn")).click()
    }

    fun findEarliestEnabled() {
        lookForTimeSlot { driver.findElement(By.className("deliveryTimeRight")).click() }
    }

    private fun lookForTimeSlot(selectYourSlot: () -> Unit) {
        var foundSlot = false
        while (!foundSlot) {
            try {
                selectYourSlot()
                val firstAvailable = driver.findElements(By.className("slot-item-timing-row"))
                    .firstOrNull() { !it.getAttribute("class").contains("disabled") }

                firstAvailable?.let {
                    println("${LocalDateTime.now()} Yey found a slot...")
                    playSound(10, "pass.wav")
                    foundSlot = true
                } ?: run {
                    val sleepTimeMin: Long = 1
//                    playSound(1, "fail.wav", -60.0f)
                    println("${LocalDateTime.now()} Did not find any time slot...")
                    println("Sleeping for $sleepTimeMin ")
                    Thread.sleep(sleepTimeMin * 60 * 10)
                    driver.findElement(By.className("rm-slots-page-modal-header-close")).click()
                    foundSlot = false
                    println("Sleeping for $sleepTimeMin ")
                    Thread.sleep(sleepTimeMin * 60 * 10)
                }
            }catch (e : Exception){
                println("$e during refresh and check ")
                playSound(1, "fail.wav", -20.0f)
                break
            }
        }
    }

}