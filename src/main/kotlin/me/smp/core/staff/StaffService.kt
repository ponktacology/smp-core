package me.smp.core.staff

import me.smp.core.TaskDispatcher
import me.smp.core.rank.RankService
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class StaffService : KoinComponent {

    private val plugin: JavaPlugin by inject()
    private val staffSettingsRepository: StaffSettingsRepository by inject()
    private val rankService: RankService by inject()

    fun getByPlayer(player: Player) = staffSettingsRepository.getByPlayer(player)

    fun getByOnlinePlayer(player: Player) = staffSettingsRepository.getByOnlinePlayer(player)

    fun updateGod(player: Player, state: Boolean) {
        val staffSettings = getByPlayer(player)

        if (staffSettings.god != state) {
            staffSettings.god = state
            staffSettings.flushChanges()
        }
    }

    private fun updateFlyInternal(player: Player, state: Boolean) {
        if (state) {
            player.allowFlight = true
            player.isFlying = true
        } else if (player.gameMode != GameMode.CREATIVE && player.gameMode != GameMode.SPECTATOR) {
            player.isFlying = false
            player.allowFlight = false
        }
    }

    fun updateFly(player: Player, state: Boolean) {
        val staffSettings = getByPlayer(player)

        if (staffSettings.fly != state) {
            staffSettings.fly = state
            staffSettings.flushChanges()
        }

        TaskDispatcher.dispatch { updateFlyInternal(player, state) }
    }

    private fun updateVanishInternal(player: Player, state: Boolean) {
        val isStaff = rankService.getByPlayer(player).isStaff()

        player.isCollidable = !state

        Bukkit.getOnlinePlayers().forEach {
            if (!rankService.getByPlayer(it).isStaff()) {
                if (state) {
                    it.hidePlayer(plugin, player)
                } else it.showPlayer(plugin, player)
            } else if (!isStaff) {
                it.showPlayer(plugin, player)
                val otherStaffSettings = staffSettingsRepository.getByOnlinePlayer(it)

                if (otherStaffSettings.vanish) {
                    player.hidePlayer(plugin, it)
                }
            }
        }
    }

    fun updateVanish(player: Player, state: Boolean) {
        val staffSettings = getByPlayer(player)

        if (staffSettings.vanish != state) {
            staffSettings.vanish = state
            staffSettings.flushChanges()
        }

        TaskDispatcher.dispatch { updateVanishInternal(player, state) }
    }

    fun applyToPlayer(player: Player) {
        val staffSettings = getByOnlinePlayer(player)
        updateFlyInternal(player, staffSettings.fly)
        updateVanishInternal(player, staffSettings.vanish)
    }
}
