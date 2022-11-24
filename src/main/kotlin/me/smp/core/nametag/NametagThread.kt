package me.smp.core.nametag

import java.util.concurrent.ConcurrentHashMap

internal class NametagThread : Thread("qLib - Nametag Thread") {
    init {
        this.isDaemon = true
    }

    override fun run() {
        while (true) {
            for (pendingUpdate in pendingUpdates.keys) {
                try {
                    FrozenNametagHandler.applyUpdate(pendingUpdate)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            try {
                sleep(FrozenNametagHandler.updateInterval * 50L)
            } catch (e2: InterruptedException) {
                e2.printStackTrace()
            }
        }
    }

    companion object {
        @JvmStatic
        val pendingUpdates: MutableMap<NametagUpdate, Boolean> = ConcurrentHashMap()
    }
}
