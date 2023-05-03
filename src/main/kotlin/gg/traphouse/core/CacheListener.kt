package gg.traphouse.core

import gg.traphouse.core.cooldown.CooldownRepository
import gg.traphouse.core.player.PlayerLookupRepository
import gg.traphouse.core.pm.PrivateMessageRepository
import gg.traphouse.core.punishment.PunishmentRepository
import gg.traphouse.core.rank.RankRepository
import gg.traphouse.core.scoreboard.ScoreboardRepository
import gg.traphouse.core.staff.StaffSettingsRepository
import gg.traphouse.core.staff.freeze.FreezeRepository
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.logging.Level
import java.util.logging.Logger

class CacheListener : Listener, KoinComponent {

    private val logger: Logger by inject()
    private val rankRepository: RankRepository by inject()
    private val punishmentRepository: PunishmentRepository by inject()
    private val playerLookupRepository: PlayerLookupRepository by inject()
    private val privateMessageRepository: PrivateMessageRepository by inject()
    private val cooldownRepository: CooldownRepository by inject()
    private val scoreboardRepository: ScoreboardRepository by inject()
    private val freezeRepository: FreezeRepository by inject()
    private val staffSettingsRepository: StaffSettingsRepository by inject()

    private val cacheList = listOf<UUIDCache>(
        rankRepository,
        privateMessageRepository,
        cooldownRepository,
        staffSettingsRepository
    )

    @EventHandler(priority = EventPriority.LOW)
    fun onPlayerLogin(event: AsyncPlayerPreLoginEvent) {
        try {
            cacheList.forEach {
                it.loadCache(event.uniqueId)
            }
            val address = event.address.toString().replace("/", "")
            punishmentRepository.loadCache(event.uniqueId, address)
            playerLookupRepository.loadCache(event.uniqueId, event.name, address)
        } catch (e: Exception) {
            e.printStackTrace()
            event.disallow(
                AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                Component.text(
                    "\nError while loading player data!\nIf this error repeats, please contact staff at dc.traphouse.gg",
                    NamedTextColor.RED
                )
            )
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        event.joinMessage(Component.empty())
        cacheList.forEach {
            if (!it.verifyCache(player.uniqueId)) {
                logger.log(Level.WARNING, "Player failed ${it.javaClass.simpleName} verification.")
                player.kick(
                    Component.text(
                        "Error while verifying player data!\nIf this error repeats, please contact staff at dc.traphouse.gg",
                        NamedTextColor.RED
                    )
                )
                return
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerLoginDisallowed(event: AsyncPlayerPreLoginEvent) {
        if (event.loginResult != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            cacheList.forEach { it.flushCache(event.uniqueId) }
            punishmentRepository.flushCache(event.uniqueId)
            scoreboardRepository.flushCache(event.uniqueId)
            freezeRepository.flushCache(event.uniqueId)
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val uuid = event.player.uniqueId
        event.quitMessage(Component.empty())
        cacheList.forEach { it.flushCache(uuid) }
        punishmentRepository.flushCache(uuid)
        scoreboardRepository.flushCache(uuid)
        freezeRepository.flushCache(uuid)
    }
}
