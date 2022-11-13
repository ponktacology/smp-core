package me.smp.core.event

import org.bukkit.event.Event
import org.bukkit.event.HandlerList

open class BasicEvent(async: Boolean) : Event(async) {

    companion object {
        private val handlerList = HandlerList()

        @JvmName("getHandlerList")
        @JvmStatic
        fun getHandlerList() = handlerList
    }

    override fun getHandlers() = handlerList
}
