package me.smp.core

import org.bukkit.configuration.file.FileConfiguration
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.TimeUnit

object Config : KoinComponent {

    private val config: FileConfiguration by inject()

    val NAME_CACHE_EXPIRY_SECONDS = TimeUnit.SECONDS.convert(1, TimeUnit.DAYS)
    val ADDRESS_CACHE_EXPIRY_SECONDS = TimeUnit.SECONDS.convert(1, TimeUnit.DAYS)
    val SERVER_NAME
        get() = config.get("server_name")
}
