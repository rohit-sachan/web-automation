package org.home.lazada

import org.home.util.alerts.Sound.playSound
import java.lang.Exception


fun main(args: Array<String>) {
    val slotsPage = LazadaMainPage()
    var stop = false
    while (!stop) {
        try {
            slotsPage.openAndLogin("https://lazada.sg", "abc@gmail.com", "xxxx")
            slotsPage.clickOnCart()
            slotsPage.findEarliestEnabled()
        } catch (e: Exception) {
            println("some bigger exception ... $e")
            playSound(2, "fail.wav")
        }
    }

}