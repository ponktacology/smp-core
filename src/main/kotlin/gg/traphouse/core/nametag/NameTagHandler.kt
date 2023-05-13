package gg.traphouse.core.nametag

import gg.traphouse.core.nametag.NametagThread.Companion.pendingUpdates
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.util.concurrent.ConcurrentHashMap

object NameTagHandler {
    private val nameTags = ConcurrentHashMap<NameTagInfo, NameTag>()
    private val teamMap: MutableMap<String, MutableMap<String, NameTag>> = ConcurrentHashMap()
    private var teamCreateIndex = 1
    private val providers: MutableList<NametagProvider> = ArrayList()
    var isInitiated = false
        private set
    var isAsync = true
    var updateInterval = 20
    fun init(plugin: JavaPlugin) {
        isInitiated = true
        NametagThread().start()
        plugin.server.pluginManager.registerEvents(NametagListener(), plugin)
    }

    fun registerProvider(newProvider: NametagProvider) {
        providers.add(newProvider)
        providers.sortBy { it.weight }
    }

    fun reloadPlayer(toRefresh: Player) {
        val update = NametagUpdate(toRefresh)
        if (isAsync) {
            pendingUpdates.offer(update)
        } else {
            applyUpdate(update)
        }
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
        if (isAsync) {
            pendingUpdates.offer(update)
        } else {
            applyUpdate(update)
        }
    }

    internal fun applyUpdate(nametagUpdate: NametagUpdate) {
        val toRefreshPlayer = Bukkit.getServer().getPlayerExact(nametagUpdate.toRefresh) ?: return
        if (!toRefreshPlayer.isOnline || Bukkit.isStopping()) return
        if (nametagUpdate.refreshFor == null) {
            for (refreshFor in Bukkit.getServer().onlinePlayers) {
                reloadPlayerInternal(toRefreshPlayer, refreshFor)
            }
        } else {
            val refreshForPlayer = Bukkit
                .getServer()
                .getPlayerExact(nametagUpdate.refreshFor!!)
            if (refreshForPlayer != null && refreshForPlayer.isOnline) {
                reloadPlayerInternal(toRefreshPlayer, refreshForPlayer)
            }
        }
    }

    private fun reloadPlayerInternal(toRefresh: Player?, refreshFor: Player?) {
        if (refreshFor == null || toRefresh == null) return
        val nameTagInfo = NameTagInfo(Component.empty(), Component.empty(), ChatColor.WHITE)
        providers.forEach { it.modifyNameTag(nameTagInfo, toRefresh, refreshFor) }
        var teamInfoMap: MutableMap<String, NameTag> = HashMap()
        // if (teamMap.containsKey(refreshFor.name)) {
        //   teamInfoMap = teamMap[refreshFor.name]!!
        // }

        val finalProvided = nameTags.computeIfAbsent(nameTagInfo) {
            val nameTag = NameTag(teamCreateIndex++.toString(), nameTagInfo)
            Bukkit.getOnlinePlayers().forEach {
                nameTag.teamAddPacket.sendToPlayer(it)
            }
            return@computeIfAbsent nameTag
        }
        println(finalProvided.info.color)
        ScoreboardTeamPacketMod(
            finalProvided.name,
            toRefresh.name,
            3
        )
            .sendToPlayer(refreshFor)
        //  teamInfoMap[toRefresh.name] = finalProvided
        //teamMap[refreshFor.name] = teamInfoMap
    }

    internal fun initiatePlayer(player: Player?) {
        for (teamInfo in nameTags.elements()) {
            teamInfo.teamAddPacket.sendToPlayer(player)
        }
    }

}
