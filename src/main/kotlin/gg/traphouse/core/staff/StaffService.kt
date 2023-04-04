package gg.traphouse.core.staff

import gg.traphouse.core.Plugin
import gg.traphouse.core.TaskDispatcher
import gg.traphouse.core.nametag.FrozenNametagHandler
import gg.traphouse.core.rank.RankService
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class StaffService : KoinComponent {

    private val plugin: Plugin by inject()
    private val staffSettingsRepository: StaffSettingsRepository by inject()
    private val rankService: RankService by inject()

    fun getByOnlinePlayer(player: Player) = staffSettingsRepository.getByOnlinePlayer(player)

    fun toggleGod(player: Player): Boolean {
        val staffSettings = getByOnlinePlayer(player)
        staffSettings.god = !staffSettings.god
        return staffSettings.god
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

    fun toggleFly(player: Player): Boolean {
        val staffSettings = getByOnlinePlayer(player)
        staffSettings.fly = !staffSettings.fly
        updateFlyInternal(player, staffSettings.fly)
        return staffSettings.fly
    }

    private fun updateVanishInternal(player: Player, state: Boolean) {
        val isStaff = rankService.getByPlayer(player).isStaff()

        player.isCollidable = !state
        player.sendActionBar(Component.empty())

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

    fun toggleVanish(player: Player): Boolean {
        val staffSettings = getByOnlinePlayer(player)
        staffSettings.vanish = !staffSettings.vanish
        // TODO: DO THIS ON MAIN THREAD
        FrozenNametagHandler.reloadPlayer(player)
        updateVanishInternal(player, staffSettings.vanish)
        return staffSettings.vanish    }

    fun applyToPlayer(player: Player) {
        val staffSettings = getByOnlinePlayer(player)
        updateFlyInternal(player, staffSettings.fly)
        updateVanishInternal(player, staffSettings.vanish)
    }
}
