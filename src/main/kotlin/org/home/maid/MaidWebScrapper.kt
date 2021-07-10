package org.home.maid

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

fun main() {
    val netMaid = NetMaidMainPage()
    val netMaidSiteUrl = "https://www.netmaid.com.sg"
    netMaid.openAndLogin("$netMaidSiteUrl/users/sign_in", "abc@gmail.com", "xxx")
    val helpersNetMaid = netMaid.findHelper(netMaidSiteUrl, "7", listOf<String>("india","cook"), lastPage = 10)

//    val bestMaid = BestMaidMainPage()
//    val bestMaidSiteUrl = "https://bestmaid.com.sg/"
//    bestMaid.openAndLogin("$bestMaidSiteUrl/employer-login.php", "abc@gmail.com", "xxxx")
//    val helpersBestMaid = bestMaid.findHelper(bestMaidSiteUrl, "7", listOf<String>("india"), lastPage = 10)
    val helpersBestMaid = emptyArray<MaidDetails>()


    val ignoreHelpers = loadIgnoreList()
    (helpersNetMaid + helpersBestMaid)
        .filter { !ignoreHelpers.contains(it.url) }
        .sortedByDescending { it.updateTime }
        .forEach{
            println(
                String.format(
                    "|%-20s|%-100s|%-25s|%-10s|", it.updateTime, it.url, it.name, it.refCode
                )
            )
            addToVisitedList(it.url.trim()+"\n")
        }

}

fun addToVisitedList(url: String) {
    Files.write(Paths.get("C:\\projects\\lazada-automation\\src\\main\\resources\\maid-visited-list.txt"), url.toByteArray(), StandardOpenOption.APPEND)
}
fun loadIgnoreList(): MutableSet<String> {
    val res = mutableSetOf<String>()
    File("C:\\projects\\lazada-automation\\src\\main\\resources\\maid-ignore-list.txt").forEachLine { res.add(it.trim()) }
    File("C:\\projects\\lazada-automation\\src\\main\\resources\\maid-visited-list.txt").forEachLine { res.add(it.trim()) }
    return res
}
