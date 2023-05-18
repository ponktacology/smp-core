package gg.traphouse.core

import gg.traphouse.shared.Duration

interface Expiring {

    var startedAt: Long
    val duration: Duration

    fun hasExpired() = !duration.isPermanent() && System.currentTimeMillis() >= expiringAt()

    fun hasNotExpired() = !hasExpired()

    fun expiringAt() = if (duration.isPermanent()) -1 else duration + startedAt

    fun reset() {
        startedAt = System.currentTimeMillis()
    }

    fun expiresIn() = expiringAt() - System.currentTimeMillis()
}
