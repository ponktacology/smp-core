package me.smp.core.vanish

import me.smp.core.TaskDispatcher
import me.smp.core.rank.RankService
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class VanishService : KoinComponent {

    private val plugin: JavaPlugin by inject()
    private val vanishRepository: VanishRepository by inject()
    private val rankService: RankService by inject()

    fun getByOnlinePlayer(player: Player) = vanishRepository.getByOnlinePlayer(player)

    fun updateVanish(player: Player) {
        val vanishSettings = getByOnlinePlayer(player)
        updateVanish(player, vanishSettings.enabled)
    }

    fun updateVanish(player: Player, enabled: Boolean) {
        val vanishSettings = getByOnlinePlayer(player)

        if (vanishSettings.enabled != enabled) {
            vanishSettings.enabled = enabled
            vanishSettings.flushChanges()
        }

        val isStaff = rankService.getByPlayer(player).isStaff()
        TaskDispatcher.dispatch {
            Bukkit.getOnlinePlayers().forEach {
                if (rankService.getByPlayer(it).isStaff()) {
                    val otherVanishSettings = getByOnlinePlayer(it)

                    if (otherVanishSettings.enabled && !isStaff) {
                        player.hidePlayer(plugin, it)
                        println("HIDING ${it.name} to ${player.name}")
                    }
                }

                if (vanishSettings.enabled) {
                    it.hidePlayer(plugin, player)
                    println("HIDING ${player.name} to ${it.name}")
                } else {
                    it.showPlayer(plugin, player)
                }
            }
        }
    }
}
