package me.smp.core.nametag

import com.google.common.collect.Lists
import com.google.common.primitives.Ints
import me.smp.core.TaskDispatcher.dispatchAsync
import me.smp.core.nametag.NametagThread.Companion.pendingUpdates
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object FrozenNametagHandler {
    private val teamMap: MutableMap<String, MutableMap<String, NametagInfo>> = ConcurrentHashMap()
    private val registeredTeams = Collections.synchronizedList(Lists.newArrayList<NametagInfo>())
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
        providers.sortWith { a: NametagProvider, b: NametagProvider -> Ints.compare(b.weight, a.weight) }
    }

    fun reloadPlayer(toRefresh: Player) {
        val update = NametagUpdate(toRefresh)
        if (isAsync) {
            pendingUpdates.offer(update)
        } else {
            applyUpdate(update)
        }
    }

    fun reloadOthersFor(refreshFor: Player) {
        for (toRefresh in Bukkit.getServer().onlinePlayers) {
            if (refreshFor === toRefresh) continue
            reloadPlayer(toRefresh, refreshFor)
        }
    }

    fun reloadPlayer(toRefresh: Player, refreshFor: Player) {
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
            if (refreshForPlayer != null) {
                reloadPlayerInternal(toRefreshPlayer, refreshForPlayer)
            }
        }
    }

    internal fun reloadPlayerInternal(toRefresh: Player?, refreshFor: Player?) {
        if (refreshFor == null || toRefresh == null) return
        var provided: NametagInfo? = null
        var providerIndex = 0
        while (provided == null) {
            provided = providers[providerIndex++].fetchNametag(toRefresh, refreshFor)
        }
        var teamInfoMap: MutableMap<String, NametagInfo> = HashMap()
        if (teamMap.containsKey(refreshFor.name)) {
            teamInfoMap = teamMap[refreshFor.name]!!
        }
        val finalProvided: NametagInfo = provided
        dispatchAsync {
            ScoreboardTeamPacketMod(
                finalProvided.name,
                listOf(toRefresh.name),
                3
            )
                .sendToPlayer(refreshFor)
        }
        teamInfoMap[toRefresh.name] = provided
        teamMap[refreshFor.name] = teamInfoMap
    }

    internal fun initiatePlayer(player: Player?) {
        for (teamInfo in registeredTeams) {
            teamInfo.teamAddPacket.sendToPlayer(player)
        }
    }

    internal fun getOrCreate(prefix: Component, suffix: Component, color: ChatColor): NametagInfo {
        for (teamInfo in registeredTeams) {
            if (teamInfo.prefix != prefix || teamInfo.suffix != suffix) continue
            return teamInfo
        }
        val newTeam = NametagInfo(teamCreateIndex++.toString(), prefix, suffix, color)
        registeredTeams.add(newTeam)
        val addPacket = newTeam.teamAddPacket
        for (player in Bukkit.getOnlinePlayers()) {
            addPacket.sendToPlayer(player)
        }
        return newTeam
    }

    internal fun getTeamMap(): MutableMap<String, MutableMap<String, NametagInfo>> {
        return teamMap
    }
}
