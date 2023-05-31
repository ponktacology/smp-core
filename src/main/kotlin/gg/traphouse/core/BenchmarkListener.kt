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
    fun onFirst(event: AsyncPlayerPreLoginEvent) {
        timings[event.uniqueId] = System.currentTimeMillis()
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onLast(event: AsyncPlayerPreLoginEvent) {
        if (event.loginResult != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            timings.remove(event.uniqueId)
            return
        }

        timings[event.uniqueId] = System.currentTimeMillis() - (timings[event.uniqueId] ?: 0L)
    }

    @EventHandler
    fun onPlayerJoinLast(event: PlayerJoinEvent) {
        val elapsed = timings.remove(event.player.uniqueId) ?: return
        event.player.sendSuccess("Załadowane twoje dane w ${elapsed}ms.")
    }

}
