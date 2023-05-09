package gg.traphouse.core

import org.bukkit.Bukkit
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.CompletableFuture

typealias BatchTask = List<CompletableFuture<Void>>

object TaskDispatcher : KoinComponent {

    private val plugin: Plugin by inject()

    fun dispatchAsync(runnable: Runnable) {
        if (!Bukkit.isPrimaryThread()) {
            runnable.run()
            return
        }

        Bukkit.getServer().scheduler.runTaskAsynchronously(plugin, runnable)
    }

    fun dispatch(runnable: Runnable) {
        if (Bukkit.isPrimaryThread()) {
            runnable.run()
            return
        }

        Bukkit.getServer().scheduler.runTask(plugin, runnable)
    }

    fun dispatchLater(runnable: Runnable, ticks: Long) {
        Bukkit.getServer().scheduler.runTaskLater(plugin, runnable, ticks)
    }

    fun runRepeatingAsync(runnable: Runnable, ticks: Long) {
        Bukkit.getServer().scheduler.runTaskTimerAsynchronously(plugin, runnable, 0L, ticks)
    }

    fun runBatch(runnables: Collection<Runnable>): BatchTask {
        return runnables.map { CompletableFuture.runAsync(it) }
    }

    fun BatchTask.awaitAll() {
        return this.forEach { it.join() }
    }
    fun runRepeating(runnable: Runnable, ticks: Long) =
        Bukkit.getServer().scheduler.runTaskTimer(plugin, runnable, 0L, ticks)
}


