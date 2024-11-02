package org.home.maid

import org.openqa.selenium.By
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.remote.RemoteWebElement
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.support.ui.Select
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class NetMaidMainPage {
    private var driver: ChromeDriver
    init {
        System.setProperty("webdriver.gecko.driver", "chromedriver_win32\\chromedriver.exe");
//        driver = FirefoxDriver()
        driver = ChromeDriver()
    }
    fun openAndLogin(url: String, userID: String, password: String) {
        driver.get(url)
        Thread.sleep(1000)

//        val navBarForUser = driver.findElement(By.id(("navbarDropdown").click()
//        driver.findElement(By.className("dropdown-item").click();

        driver.findElement(By.id(("user_login"))).sendKeys(userID)

        driver.findElement(By.id(("user_password_sessions"))).sendKeys(password)

        driver.findElement(By.className("btn-success")).click()

        Thread.sleep(2000)
        println("logged-in")
    }

    fun findHelper(baseUrl: String, profileFreshness: String, keyWords: List<String>, lastPage: Int): MutableList<MaidDetails> {
        driver.navigate().to("https://www.netmaid.com.sg/search")
        println("switched to search ... ")
        Thread.sleep(1000)
        Select(driver.findElement(By.id(("netmaid_search_created")))).selectByValue(profileFreshness)

//        driver.findElement(By.id(("netmaid_search_nationality_PH").click()
        driver.findElement(By.id(("netmaid_search_nationality_ID"))).click()
//        driver.findElement(By.id(("netmaid_search_nationality_LK").click()
//        driver.findElement(By.id(("netmaid_search_nationality_MM").click()
//        driver.findElement(By.id(("netmaid_search_nationality_IN").click()
        driver.findElement(By.id(("netmaid_search_mtype_TXF"))).click()

        driver.findElement(By.className("btn-success")).click()

        val results = mutableListOf<MaidDetails>()
        val js = driver as JavascriptExecutor
        js.executeScript("window.scrollTo(0, document.body.scrollHeight)")

        val helpersOnPage = driver.findElements(By.className("maid_overview"))
        helpersOnPage.forEach { helper ->
            Thread.sleep(1000)
            val helperAbsURL = getAbsURLForHelper(helper, baseUrl)
            val helperExperience = helper.getAttribute("innerHTML").toString()
            val match = keyWords.all {expKeyword ->
                helperExperience.contains(expKeyword, true)
            }
            if(match){
                val maidDetails = MaidDetails(helperAbsURL, helperExperience)
                val doc= Jsoup.connect(helperAbsURL).get()
                enrichMaidDetails(doc, maidDetails)
                results.add(maidDetails)
            }
        }
        return results
    }

    private fun getAbsURLForHelper(helper: WebElement?, baseUrl: String): String {
        val helperThumbNail = Jsoup.parse((helper as RemoteWebElement).getAttribute("innerHTML"))
        val helperRelativeURL = helperThumbNail.select("a[href]").first().attributes()["href"]
        return baseUrl + helperRelativeURL
    }

    private fun enrichMaidDetails(helperPage: Document, maidDetails: MaidDetails): MaidDetails {
        val updateTime = LocalDateTime.parse(helperPage.getElementsMatchingOwnText("Last updated on ").text().replace("Last updated on ",""),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss Z"))

        helperPage.getElementById("maid_detail").childNodes().forEach { node ->
            node.attributes()["class"]?.let { attrVal ->
                val attributeNode = Jsoup.parse(node.toString())
                if (attrVal == "line") {
                    val textToSearch = attributeNode.select("div").first().text()
                    if (textToSearch.contains("Maid Name")) {
                        maidDetails.name = textToSearch.replace("Maid Name", "")
                    }
                    if (textToSearch.contains("Ref. Code")) {
                        maidDetails.refCode = textToSearch.replace("Ref. Code", "")
                    }
                    if (textToSearch.contains("Maid Agency")) {
                        maidDetails.refCode = textToSearch.replace("Maid Agency", "")
                    }
                }
            }
        }
        maidDetails.updateTime = updateTime
        return maidDetails
    }
}

