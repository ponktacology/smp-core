package gg.traphouse.core.nametag

import gg.traphouse.core.Config
import java.util.concurrent.ConcurrentLinkedQueue

internal class NameTagThread : Thread("NameTag Thread") {

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
                sleep(Config.NAMETAG_UPDATE_INTERVAL * 50L)
            } catch (e2: InterruptedException) {
                e2.printStackTrace()
            }
        }
    }

    companion object {
        @JvmStatic
        val pendingUpdates = ConcurrentLinkedQueue<NameTagUpdate>()
    }
}
