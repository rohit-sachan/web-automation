package org.home.maid
import org.openqa.selenium.By
import org.openqa.selenium.firefox.FirefoxDriver

class NetMaidOpener {
    fun main(args: Array<String>) {
    val driver = FirefoxDriver()
    driver.get("https://www.netmaid.com.sg")

    val usernameField = driver.findElement(By.id("usernameFieldId")) // replace with actual id
    usernameField.sendKeys("sachan")

    val passwordField = driver.findElement(By.id("passwordFieldId")) // replace with actual id
    passwordField.sendKeys("abcd")

    val loginButton = driver.findElement(By.id("loginButtonId")) // replace with actual id
    loginButton.click()
    }

}