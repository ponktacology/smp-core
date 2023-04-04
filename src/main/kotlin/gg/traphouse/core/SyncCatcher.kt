package gg.traphouse.core

import org.bukkit.Bukkit
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.logging.Level
import java.util.logging.Logger

object SyncCatcher : KoinComponent {

    private val logger: Logger by inject()

    fun verify() {
        if (Bukkit.isPrimaryThread()) throw IllegalStateException("This method can't be called from main thread")
      //  val stackTrace = Throwable().stackTrace
        //logger.log(Level.WARNING, "ASYNC= ${stackTrace[1].className}.${stackTrace[1].methodName} THREAD= ${Thread.currentThread().name}")
    }
}
