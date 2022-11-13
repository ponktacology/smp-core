package me.smp.core

import java.util.*

interface Manageable {
    val id: Int
    var player: UUID
    var issuer: UUID
    var reason: String
    var duration: Duration
    var addedAt: Long
    var removed: Boolean
    var removedAt: Long?
    var remover: UUID?
    var removeReason: String?

    fun isActive() = !(removed || (!duration.isPermanent() && duration + addedAt < System.currentTimeMillis()))
}
