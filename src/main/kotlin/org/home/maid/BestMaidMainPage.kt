package org.home.maid

import org.openqa.selenium.By
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.remote.RemoteWebElement
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.Select
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class BestMaidMainPage {
    private var driver: FirefoxDriver
    init {
        System.setProperty("webdriver.gecko.driver", "geckodriver-v0.26.0-win64\\geckodriver.exe");
        driver = FirefoxDriver()
    }
    fun openAndLogin(url: String, userID: String, password: String) {
        driver.get(url)
        Thread.sleep(1000)
        driver.findElementById("defaultOpen").click()


        driver.findElementById("email_login").sendKeys(userID)
        driver.findElementById("password_login").sendKeys(password)
        driver.findElementById("submit_login").click()
        println("logged-in")
    }

    fun findHelper(baseUrl: String, profileFreshness: String, keyWords: List<String>, lastPage: Int): MutableList<MaidDetails> {
        driver.navigate().to("https://bestmaid.com.sg/searchmaid.asp")
        println("switched to search ... ")
        driver.findElementByClassName("input_510").sendKeys(keyWords.joinToString())
        Select(driver.findElementByClassName("select_180")).selectByValue(profileFreshness)
        driver.findElementsByName("maid-type")[1].click()


        driver.findElementById("na1").click()
        driver.findElementById("na2").click()
        driver.findElementById("na4").click()

        driver.findElementsByClassName("button_green").get(3).click()

        val results = mutableListOf<MaidDetails>()

        (1..lastPage).forEach {


            val pagedResultUrl = "https://bestmaid.com.sg/maid_display.php?page=$it"
            val helpersOnPage = Jsoup.connect(pagedResultUrl).get().getElementsByClass("logout-maid")
            helpersOnPage.forEach { helper ->
                println(helper)
                val helperExperience = helper.getElementsByClass("popup-content")

//                val match = keyWords.all {expKeyword ->
//                    helperExperience.contains(expKeyword, true)
//                }
//                val helperAbsURL = getAbsURLForHelper(helper, baseUrl)
//                val helperExperience = helper.getAttribute("innerHTML").toString()
//                if(match){
//                    val maidDetails = MaidDetails(helperAbsURL, helperExperience)
//                    val doc= Jsoup.connect(helperAbsURL).get()
//                    enrichMaidDetails(doc, maidDetails)
//                    results.add(maidDetails)
//                }
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