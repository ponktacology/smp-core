package gg.traphouse.core

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class BenchmarkListener : Listener {

    private val timings = ConcurrentHashMap<UUID, Long>()

    @EventHandler(priority = EventPriority.LOWEST)
    fun onAsyncLoginFirst(event: AsyncPlayerPreLoginEvent) {
        timings[event.uniqueId] = System.currentTimeMillis()
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onAsyncLoginLast(event: AsyncPlayerPreLoginEvent) {
        println("Player login ${event.name} took ${System.currentTimeMillis() - (timings[event.uniqueId] ?: System.currentTimeMillis())}")

        if (event.loginResult != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            timings.remove(event.uniqueId)
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerJoinFirst(event: PlayerJoinEvent) {
        timings[event.player.uniqueId] = System.currentTimeMillis()
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerJoinLast(event: PlayerJoinEvent) {
        println("Player join ${event.player.name} took ${System.currentTimeMillis() - (timings[event.player.uniqueId] ?: System.currentTimeMillis())}")
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        timings.remove(event.player.uniqueId)
    }
}
