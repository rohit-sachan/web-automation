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
        driver.findElementById("anonLogin").click()
        driver.findElementByClassName("mod-login-input-loginName")
            .findElement(By.ByXPath("//input[@type='text']"))
            .sendKeys(userID)

        driver.findElementByClassName("mod-login-input-password")
            .findElement(By.ByXPath("//input[@type='password']"))
            .sendKeys(password)

        driver.findElementByClassName("next-btn-primary").click()

    }


    private fun close() {
        driver.close()
    }

    fun clickOnCart() {
        driver.findElementById("topActionCartNumber").click()
        driver.findElementByClassName("checkout-shop-checkbox").click()
        var clicked = false
        while (!clicked)
            try {
                driver.findElementByClassName("automation-checkout-order-total-button-button").click()
                clicked = true
            } catch (e: Exception) {
                println("some exception trying again ... ")
            }
        driver.findElementByClassName("got-it-btn").click()
    }

    fun findEarliestEnabled() {
        lookForTimeSlot { driver.findElementByClassName("deliveryTimeRight").click() }
    }

    private fun lookForTimeSlot(selectYourSlot: () -> Unit) {
        var foundSlot = false
        while (!foundSlot) {
            try {
                selectYourSlot()
                val firstAvailable = driver.findElementsByClassName("slot-item-timing-row")
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
                    driver.findElementByClassName("rm-slots-page-modal-header-close").click()
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