package gg.traphouse.core.nametag

import gg.traphouse.core.nametag.NameTagThread.Companion.pendingUpdates
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.util.concurrent.ConcurrentHashMap

object NameTagHandler {

    const val UPDATE_INTERVAL = 10
    private val nameTags = ConcurrentHashMap<NameTagInfo, NameTag>()
    private val providers: MutableList<NameTagProvider> = ArrayList()
    private var teamCreateIndex = 1u

    fun init(plugin: JavaPlugin) {
        NameTagThread().start()
        plugin.server.pluginManager.registerEvents(NameTagListener(), plugin)
    }

    fun registerProvider(newProvider: NameTagProvider) {
        providers.add(newProvider)
        providers.sortBy { it.weight }
    }

    fun reloadPlayer(refreshFor: Player) {
        for (toRefresh in Bukkit.getServer().onlinePlayers) {
            reloadPlayer(toRefresh, refreshFor)
            reloadPlayer(refreshFor, toRefresh)
        }
    }

    private fun reloadPlayer(toRefresh: Player, refreshFor: Player) {
        val update = NameTagUpdate(toRefresh.uniqueId, refreshFor.uniqueId)
        pendingUpdates.offer(update)
    }

    internal fun applyUpdate(nametagUpdate: NameTagUpdate) {
        val toRefreshPlayer = Bukkit.getPlayer(nametagUpdate.toRefresh) ?: return
        val refreshForPlayer = Bukkit.getPlayer(nametagUpdate.refreshFor) ?: return
        reloadPlayerInternal(toRefreshPlayer, refreshForPlayer)
    }

    private fun reloadPlayerInternal(toRefresh: Player, refreshFor: Player) {
        val nameTagInfo = NameTagInfo(Component.empty(), Component.empty(), ChatColor.WHITE)
        providers.forEach { it.modifyNameTag(nameTagInfo, toRefresh, refreshFor) }

        val finalProvided = nameTags.computeIfAbsent(nameTagInfo) {
            val nameTag = NameTag(teamCreateIndex++.toString(), nameTagInfo)
            Bukkit.getOnlinePlayers().forEach {
                nameTag.teamAddPacket.sendToPlayer(it)
            }
            return@computeIfAbsent nameTag
        }

        ScoreboardTeamPacketMod(
            finalProvided.name,
            toRefresh.name,
            3
        ).sendToPlayer(refreshFor)
    }

    internal fun initiatePlayer(player: Player) {
        for (teamInfo in nameTags.elements()) {
            teamInfo.teamAddPacket.sendToPlayer(player)
        }
    }

}
