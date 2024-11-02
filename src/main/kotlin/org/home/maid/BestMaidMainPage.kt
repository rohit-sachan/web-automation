package org.home.maid

import com.google.common.collect.Lists
import org.jsoup.Jsoup
import org.jsoup.nodes.TextNode
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.support.ui.Select
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.regex.Matcher
import java.util.regex.Pattern


class BestMaidMainPage {
    private var driver: FirefoxDriver

    init {
        System.setProperty("webdriver.gecko.driver", "geckodriver-v0.26.0-win64\\geckodriver.exe");
        driver = FirefoxDriver()
    }

    fun openAndLogin(url: String, userID: String, password: String) {
        driver.get(url)
        Thread.sleep(1000)
        driver.findElement(By.id(("defaultOpen"))).click()


        driver.findElement(By.id(("email_login"))).sendKeys(userID)
        driver.findElement(By.id(("password_login"))).sendKeys(password)
        driver.findElement(By.id(("submit_login"))).click()
        println("logged-in")
    }

    fun findHelper(
        baseUrl: String,
        profileFreshness: String,
        keyWords: List<String>,
        lastPage: Int
    ): MutableList<MaidDetails> {
        driver.navigate().to("https://bestmaid.com.sg/searchmaid.asp")
        println("switched to search ... ")
        driver.findElement(By.className("input_510")).sendKeys(keyWords.joinToString())
        Select(driver.findElement(By.className("select_180"))).selectByValue(profileFreshness)
        driver.findElements(By.name("maid-type"))[1].click()


//        driver.findElement(By.id(("na1").click() // Filipino
//        driver.findElement(By.id(("na2").click() // Indonesian
        driver.findElement(By.id(("na3"))).click() // Indian
//        driver.findElement(By.id(("na4").click() // Srilankan

//        driver.findElement(By.className(("button_green"))).get(3).click()

        val results = mutableListOf<MaidDetails>()

        (1..lastPage).forEach {


            val pagedResultUrl = "https://bestmaid.com.sg/maid_display.php?page=$it"
            driver.navigate().to(pagedResultUrl)
            println("clicked on $pagedResultUrl")

            val helpersResult: WebElement = driver.findElement(By.className(("ul-result")))
/*            if (helpersResult.isEmpty()) {
                return@forEach
            }

            val helpersOnPage = helpersResult.first().findElements(By.cssSelector("li"))
            val action = Actions(driver)

            helpersOnPage.forEach { helper ->
                val helperDesc = helper.findElements(By.className("maid-descript")).first()
                val helperPhoto = helper.findElements(By.className("maid-photo")).first()
                helper.findElements(By.linkText("maid"))
                action.moveToElement(helperPhoto)
                action.perform()
                val helperExperience = driver.findElement(By.className(("popover")

                val experience = helperExperience.first().text
                val name = experience.substring(0, experience.indexOf('\n'))
                println("Analysing $name")
                val match = runMatching(keyWords, experience)
                if (match.isGoodMatch) {
                    val helperAbsURL = helper.findElements(By.className("load-local")).first().getAttribute("href")
                    val maidDetails = MaidDetails(url = helperAbsURL, experience = experience, name = name)
                    enrichMaidDetails(helperDesc, maidDetails)
                    results.add(maidDetails)
                }
            }*/
        }
        val filteredList = try {results.filter { profileFreshneshMatches(it, profileFreshness) }.toMutableList()} catch (e: Exception){
            Lists.newArrayList<MaidDetails>()
        }
        return filteredList
    }

    val pattern: Pattern = Pattern.compile("^.*?(\\d{2}-\\d{2}-\\d{4}).*$")
    private fun profileFreshneshMatches(maidDetails: MaidDetails, profileFreshness: String): Boolean {
        val document = Jsoup.connect(maidDetails.url).get()
        val dateElement = document.getElementsByClass("font_669933")
        val textOfLastUpdated = dateElement.filter { it.getElementsContainingText("Last updated").hasText() }[0].childNodes()
            .asSequence()
            .filter { it.nodeName().equals("#text") }.map { it as TextNode }.map { it -> it.text() }
            .filter { it -> it.contains("Last updated") }.first()
        val matcher: Matcher = pattern.matcher(textOfLastUpdated)
        if (matcher.matches()) {
            val dateStr: String = matcher.group(1)
            val parsedDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
            val today = LocalDate.now()
            val daysDiff = ChronoUnit.DAYS.between(parsedDate, today)
            return daysDiff.toInt() <= profileFreshness.toInt()
        }
        return false
    }

    data class Match(val isGoodMatch: Boolean, val score: Int)

    private fun runMatching(keyWords: List<String>, experience: String): Match {
        val matched = keyWords.all { expKeyword ->
            experience.contains(expKeyword, true)
        }
        return Match(matched, 0)
    }

    private fun enrichMaidDetails(helperDesc: WebElement, maidDetails: MaidDetails): MaidDetails {

//        val updateTime = LocalDateTime.parse(
//            helperPage.getElementsMatchingOwnText("Last updated on ").text().replace("Last updated on ", ""),
//            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss Z")
//        )
//        maidDetails.updateTime = updateTime

        maidDetails.refCode = helperDesc.findElement(By.className("te-3")).text
        maidDetails.agency = helperDesc.findElement(By.className("agency_name")).text

        return maidDetails
    }
}