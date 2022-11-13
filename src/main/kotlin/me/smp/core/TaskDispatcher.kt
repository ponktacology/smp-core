package me.smp.core

import org.bukkit.Bukkit
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object TaskDispatcher : KoinComponent {

    private val plugin: Plugin by inject()

    fun dispatchAsync(runnable: Runnable) = Bukkit.getServer().scheduler.runTaskAsynchronously(plugin, runnable)

    fun dispatch(runnable: Runnable) = Bukkit.getServer().scheduler.runTask(plugin, runnable)

    fun dispatchLater(runnable: Runnable, ticks: Long) {
        Bukkit.getServer().scheduler.runTaskLater(plugin, runnable, ticks)
    }

    fun runRepeatingAsync(runnable: Runnable, ticks: Long) {
        Bukkit.getServer().scheduler.runTaskTimerAsynchronously(plugin, runnable, 0L, ticks)
    }
}
