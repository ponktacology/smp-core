package me.smp.core

import java.util.concurrent.TimeUnit

object Config {
    val REPORT_COOLDOWN_TIME = TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES)
    val NAME_CACHE_EXPIRY_SECONDS = TimeUnit.SECONDS.convert(1, TimeUnit.DAYS)
}