package me.smp.core

import me.smp.core.name.NameRepository
import me.smp.core.pm.PrivateMessageRepository
import me.smp.core.punishment.PunishmentRepository
import me.smp.core.rank.RankRepository
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.system.measureTimeMillis

class CacheListener : Listener, KoinComponent {

    private val logger: Logger by inject()
    private val rankRepository: RankRepository by inject()
    private val punishmentRepository: PunishmentRepository by inject()
    private val nameRepository: NameRepository by inject()
    private val privateMessageRepository: PrivateMessageRepository by inject()

    private val cacheList = listOf<UUIDCache>(rankRepository,
        punishmentRepository,
        privateMessageRepository)

    @EventHandler(priority = EventPriority.LOW)
    fun onPlayerLogin(event: AsyncPlayerPreLoginEvent) {
        val duration = measureTimeMillis {
            cacheList.forEach {
                it.loadCache(event.uniqueId)
            }
            nameRepository.loadCache(event.uniqueId, event.name)
        }
        logger.log(Level.INFO, "Loaded ${event.name} in $duration ms.")
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerLoginDisallowed(event: AsyncPlayerPreLoginEvent) {
        if (event.loginResult != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            cacheList.forEach { it.flushCache(event.uniqueId) }
        }
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        cacheList.forEach { it.flushCache(event.player.uniqueId) }
    }

}