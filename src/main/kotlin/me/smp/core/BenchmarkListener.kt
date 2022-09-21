package me.smp.core

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class BenchmarkListener : Listener {

    private val timings = ConcurrentHashMap<UUID, Long>()

    @EventHandler(priority = EventPriority.LOWEST)
    fun onAsyncLogin(event: AsyncPlayerPreLoginEvent) {
        timings[event.uniqueId] = System.currentTimeMillis()
    }


    @EventHandler(priority = EventPriority.MONITOR)
    fun onAsyncLogin2(event: AsyncPlayerPreLoginEvent) {
        println("Loaded player ${event.name} in ${System.currentTimeMillis() - (timings[event.uniqueId] ?: System.currentTimeMillis())}")
    }
}