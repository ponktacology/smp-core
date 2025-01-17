package gg.traphouse.core

import org.bukkit.configuration.file.FileConfiguration
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.TimeUnit

object Config : KoinComponent {

    private val config: FileConfiguration by inject()

    const val NAMETAG_UPDATE_INTERVAL = 20L
    const val SCOREBOARD_UPDATE_INTERVAL = 20L

    val NAME_CACHE_EXPIRY_SECONDS = TimeUnit.SECONDS.convert(1, TimeUnit.DAYS)
    val ADDRESS_CACHE_EXPIRY_SECONDS = TimeUnit.SECONDS.convert(1, TimeUnit.DAYS)

    val SERVER_NAME
        get() = config.get("server_name")
}
