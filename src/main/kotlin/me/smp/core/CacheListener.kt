package me.smp.core

import me.smp.core.cooldown.CooldownRepository
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

class CacheListener : Listener, KoinComponent {

    private val rankRepository: RankRepository by inject()
    private val punishmentRepository: PunishmentRepository by inject()
    private val nameRepository: NameRepository by inject()
    private val privateMessageRepository: PrivateMessageRepository by inject()
    private val cooldownRepository: CooldownRepository by inject()

    private val cacheList = listOf<UUIDCache>(
        rankRepository,
        privateMessageRepository
    )

    @EventHandler(priority = EventPriority.LOW)
    fun onPlayerLogin(event: AsyncPlayerPreLoginEvent) {
        cacheList.forEach {
            it.loadCache(event.uniqueId)
        }
        println("${event.address} ${event.rawAddress}")
        punishmentRepository.loadCache(event.uniqueId, event.address.toString())
        nameRepository.loadCache(event.uniqueId, event.name)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerLoginDisallowed(event: AsyncPlayerPreLoginEvent) {
        if (event.loginResult != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            cacheList.forEach { it.flushCache(event.uniqueId) }
            punishmentRepository.flushCache(event.uniqueId)
        }
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        cacheList.forEach { it.flushCache(event.player.uniqueId) }
        punishmentRepository.flushCache(event.player.uniqueId)
        cooldownRepository.flushCache(event.player.uniqueId)
    }

}