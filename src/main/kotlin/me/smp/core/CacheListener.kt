package me.smp.core

import me.smp.core.punishment.PunishmentRepository
import me.smp.core.rank.RankRepository
import me.smp.core.rank.RankService
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CacheListener : Listener, KoinComponent {

    private val rankRepository: RankRepository by inject()
    private val punishmentRepository: PunishmentRepository by inject()

    private val cacheList = listOf<UUIDCache>(rankRepository, punishmentRepository)

    @EventHandler(priority = EventPriority.LOW)
    fun onPlayerLogin(event: AsyncPlayerPreLoginEvent) {
        cacheList.forEach { it.loadCache(event.uniqueId) }
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