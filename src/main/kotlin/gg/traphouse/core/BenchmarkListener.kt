package gg.traphouse.core

import gg.traphouse.core.util.SenderUtil.sendSuccess
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerJoinEvent
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class BenchmarkListener : Listener {

    private val timings = ConcurrentHashMap<UUID, Long>()

    @EventHandler(priority = EventPriority.LOWEST)
    fun onAsyncLoginFirst(event: AsyncPlayerPreLoginEvent) {
        timings[event.uniqueId] = System.currentTimeMillis()
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerJoinLast(event: PlayerJoinEvent) {
        val loginStart = timings.remove(event.player.uniqueId) ?: return
        val elapsed = System.currentTimeMillis() - loginStart
        event.player.sendSuccess("Załadowane twoje dane w ${elapsed}ms.")
    }

}
