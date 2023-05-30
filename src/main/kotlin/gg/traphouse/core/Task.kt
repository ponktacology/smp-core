package gg.traphouse.core

import org.bukkit.Bukkit
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object Task : KoinComponent {

    private val plugin: Plugin by inject()

    fun async(runnable: Runnable) {
        if (!Bukkit.isPrimaryThread()) {
            runnable.run()
            return
        }

        Bukkit.getServer().scheduler.runTaskAsynchronously(plugin, runnable)
    }

    fun sync(runnable: Runnable) {
        if (Bukkit.isPrimaryThread()) {
            runnable.run()
            return
        }

        Bukkit.getServer().scheduler.runTask(plugin, runnable)
    }

    fun later(runnable: Runnable, ticks: Long) {
        Bukkit.getServer().scheduler.runTaskLater(plugin, runnable, ticks)
    }

    fun repeatAsync(runnable: Runnable, ticks: Long) {
        Bukkit.getServer().scheduler.runTaskTimerAsynchronously(plugin, runnable, 0L, ticks)
    }

    fun repeat(runnable: Runnable, ticks: Long) =
        Bukkit.getServer().scheduler.runTaskTimer(plugin, runnable, 0L, ticks)
}
