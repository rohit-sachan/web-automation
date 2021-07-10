package org.home.maid

import java.time.LocalDateTime

data class MaidDetails(val url: String = "", val experience: String, var name: String="", var refCode: String = "", var updateTime: LocalDateTime = LocalDateTime.now())
