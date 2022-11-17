package me.smp.core

import me.smp.core.cooldown.CooldownRepository
import me.smp.core.freeze.FreezeRepository
import me.smp.core.name.PlayerLookupRepository
import me.smp.core.pm.PrivateMessageRepository
import me.smp.core.punishment.PunishmentRepository
import me.smp.core.rank.RankRepository
import me.smp.core.scoreboard.ScoreboardRepository
import net.kyori.adventure.text.Component
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CacheListener : Listener, KoinComponent {

    private val rankRepository: RankRepository by inject()
    private val punishmentRepository: PunishmentRepository by inject()
    private val playerLookupRepository: PlayerLookupRepository by inject()
    private val privateMessageRepository: PrivateMessageRepository by inject()
    private val cooldownRepository: CooldownRepository by inject()
    private val scoreboardRepository: ScoreboardRepository by inject()
    private val freezeRepository: FreezeRepository by inject()

    private val cacheList = listOf<UUIDCache>(
        rankRepository,
        privateMessageRepository,
        cooldownRepository
    )

    @EventHandler(priority = EventPriority.LOW)
    fun onPlayerLogin(event: AsyncPlayerPreLoginEvent) {
        try {
            cacheList.forEach {
                it.loadCache(event.uniqueId)
            }
            println("${event.address} ${event.rawAddress}")
            punishmentRepository.loadCache(event.uniqueId, event.address.toString())
            playerLookupRepository.loadCache(event.uniqueId, event.name, event.address.hostName)
        } catch (e: Exception) {
            e.printStackTrace()
            event.disallow(
                AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                Component.text("Error while loading player data")
            )
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        cacheList.forEach {
            if (!it.verifyCache(player.uniqueId)) {
                player.kick(Component.text("Error while verifying player data"))
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerLoginDisallowed(event: AsyncPlayerPreLoginEvent) {
        if (event.loginResult != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            cacheList.forEach { it.flushCache(event.uniqueId) }
            punishmentRepository.flushCache(event.uniqueId)
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val uuid = event.player.uniqueId
        cacheList.forEach { it.flushCache(uuid) }
        punishmentRepository.flushCache(uuid)
        scoreboardRepository.flushCache(uuid)
        freezeRepository.flushCache(uuid)
    }
}
