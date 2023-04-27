package gg.traphouse.core.nametag

import java.util.concurrent.ConcurrentLinkedQueue

internal class NametagThread : Thread("Nametag Thread") {
    init {
        this.isDaemon = true
    }

    override fun run() {
        while (true) {
            while (pendingUpdates.peek() != null) {
                val pendingUpdate = pendingUpdates.poll()
                try {
                    NameTagHandler.applyUpdate(pendingUpdate)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            try {
                sleep(NameTagHandler.updateInterval * 50L)
            } catch (e2: InterruptedException) {
                e2.printStackTrace()
            }
        }
    }

    companion object {
        @JvmStatic
        val pendingUpdates = ConcurrentLinkedQueue<NametagUpdate>()
    }
}
