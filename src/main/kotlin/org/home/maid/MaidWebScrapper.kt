package org.home.maid

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

fun main(args: Array<String>) {
    var helpersNetMaid = mutableListOf<MaidDetails>()
    var helpersBestMaid = mutableListOf<MaidDetails>()

    when (args[0]) {
        "NetMaid" -> helpersNetMaid = findNetMaidDetails("https://www.netmaid.com.sg", helpersNetMaid)
        "BestMaid" -> helpersBestMaid = findBestMaidDetails("https://bestmaid.com.sg/", helpersBestMaid)
    }


    val ignoreHelpers = loadIgnoreList()
    (helpersNetMaid + helpersBestMaid)
        .filter { !ignoreHelpers.contains(it.url) }
        .sortedByDescending { it.updateTime }
        .forEach {
            println(
                String.format(
                    "|%-20s|%-100s|%-25s|%-10s|", it.updateTime, it.url, it.name, it.refCode
                )
            )
            addToVisitedList(it.url.trim() + "\n")
        }

}

private fun findBestMaidDetails(
    bestMaidSiteUrl: String,
    helpersBestMaid: MutableList<MaidDetails>
): MutableList<MaidDetails> {
    var helpersBestMaid1 = helpersBestMaid
    val bestMaid = BestMaidMainPage()
    bestMaid.openAndLogin("$bestMaidSiteUrl/employer-login.php", "sachan.rohit@gmail.com", "")
    helpersBestMaid1 = bestMaid.findHelper(bestMaidSiteUrl, "3", listOf<String>("india"), lastPage = 25)
    return helpersBestMaid1
}

private fun findNetMaidDetails(
    netMaidSiteUrl: String,
    helpersNetMaid: MutableList<MaidDetails>
): MutableList<MaidDetails> {
    var helpersNetMaid1 = helpersNetMaid
    val netMaid = NetMaidMainPage()
    netMaid.openAndLogin("$netMaidSiteUrl/users/sign_in", "sachan.rohit@gmail.com", "")
    helpersNetMaid1 = netMaid.findHelper(netMaidSiteUrl, "7", listOf<String>("india", "cook"), lastPage = 10)
//    helpersNetMaid1 = netMaid.findHelper(netMaidSiteUrl, "7", listOf<String>("cook"), lastPage = 10)
    return helpersNetMaid1
}

fun addToVisitedList(url: String) {
    Files.write(
        Paths.get("C:\\projects\\webautomation\\src\\main\\resources\\maid-visited-list.txt"),
        url.toByteArray(),
        StandardOpenOption.APPEND
    )
}

fun loadIgnoreList(): MutableSet<String> {
    val res = mutableSetOf<String>()
    File("C:\\projects\\webautomation\\src\\main\\resources\\maid-ignore-list.txt").forEachLine { res.add(it.trim()) }
    File("C:\\projects\\webautomation\\src\\main\\resources\\maid-visited-list.txt").forEachLine { res.add(it.trim()) }
    return res
}
