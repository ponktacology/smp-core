package me.smp.core.event

import org.bukkit.event.Cancellable

open class BasicCancellableEvent(async: Boolean) : BasicEvent(async), Cancellable {

    private var cancelled = false

    override fun isCancelled() = cancelled

    override fun setCancelled(cancel: Boolean) {
        this.cancelled = cancel
    }
}
