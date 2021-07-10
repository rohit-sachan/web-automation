package org.home.wakepark

import org.home.util.alerts.Sound.playSound
import java.lang.Exception

fun main() {
    val page =  WakeParkMainPage()
    // yyyy-mm-dd
    val date = "2021-04-18"
    page.open("https://swpsystema.youcanbook.me/service/jsps/cal.jsp?&cal=9356924e-ff2e-4361-b73a-ed57cf9ba11e&ini=1617418804806&service=jsid21231&jumpDate=$date")

    while (true) {
        try {
            if(page.refresh(date)){
                playSound(10, "pass.wav")
            }
        } catch (e: Exception) {
            println(e)
            playSound(2, "fail.wav")
        }
    }

}