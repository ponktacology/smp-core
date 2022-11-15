package me.smp.core

import me.smp.shared.Duration

interface Expiring {

    var startedAt: Long
    val duration: Duration

    fun hasExpired() = !duration.isPermanent() && System.currentTimeMillis() >= expiringAt()

    fun expiringAt() = if (duration.isPermanent()) -1 else duration + startedAt

    fun reset() {
        startedAt = System.currentTimeMillis()
    }
}
