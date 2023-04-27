package gg.traphouse.core.nametag

import gg.traphouse.core.nametag.NametagThread.Companion.pendingUpdates
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.util.concurrent.ConcurrentHashMap

object NameTagHandler {

    private val registeredTeams = ConcurrentHashMap<NametagInfo, NameTag>()
    private var teamCreateIndex = 1
    private val providers: MutableList<NametagProvider> = ArrayList()

    var updateInterval = 20

    fun init(plugin: JavaPlugin) {
        NametagThread().start()
        plugin.server.pluginManager.registerEvents(NametagListener(), plugin)
    }

    fun registerProvider(newProvider: NametagProvider) {
        providers.add(newProvider)
        providers.sortBy { it.weight }
    }

    fun reloadPlayer(toRefresh: Player) {
        val update = NametagUpdate(toRefresh)
        pendingUpdates.offer(update)
        reloadOthersFor(toRefresh)
    }

    private fun reloadOthersFor(refreshFor: Player) {
        for (toRefresh in Bukkit.getServer().onlinePlayers) {
            if (refreshFor === toRefresh) continue
            reloadPlayer(toRefresh, refreshFor)
        }
    }

    private fun reloadPlayer(toRefresh: Player, refreshFor: Player) {
        val update = NametagUpdate(toRefresh, refreshFor)
        pendingUpdates.offer(update)
    }

    internal fun applyUpdate(nameTagUpdate: NametagUpdate) {
        val toRefreshPlayer = Bukkit.getServer().getPlayer(nameTagUpdate.toRefresh) ?: return
        if (!toRefreshPlayer.isOnline || Bukkit.isStopping()) return
        val refreshFor = nameTagUpdate.refreshFor ?: let {
            Bukkit.getServer().onlinePlayers.forEach {
                refresh(toRefreshPlayer, it)
            }
            return
        }

        Bukkit.getPlayer(refreshFor)?.let {
            refresh(toRefreshPlayer, it)
        }
    }

    private fun refresh(toRefresh: Player, refreshFor: Player) {
        var info = NametagInfo(Component.empty(), Component.empty(), ChatColor.WHITE)
        providers.forEach { info = it.modifyNameTag(info, toRefresh, refreshFor) }
        val nameTag = getOrCreateNameTag(info)
        ScoreboardTeamPacketMod(
            nameTag.id,
            toRefresh.name,
            3
        ).sendToPlayer(refreshFor)
    }

    fun initiatePlayer(player: Player) {
        registeredTeams.values.forEach { it.scoreboardPacket.sendToPlayer(player) }
        reloadPlayer(player)
        reloadOthersFor(player)
    }

    private fun getOrCreateNameTag(info: NametagInfo): NameTag {
        registeredTeams[info]?.let {
            return it
        }
        val tag = NameTag(teamCreateIndex++.toString(), info)
        registeredTeams[info] = tag
        Bukkit.getOnlinePlayers().forEach { tag.scoreboardPacket.sendToPlayer(it) }
        return tag
    }
}
