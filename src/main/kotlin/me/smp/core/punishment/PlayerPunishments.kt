package me.smp.core.punishment

import me.smp.shared.punishment.Punishment
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

class PlayerPunishments {

    private val punishments = CopyOnWriteArrayList<Punishment>()

    fun findActive(type: Punishment.Type): Punishment? {
        return punishments.find { it.type == type && it.isActive() }
    }

    fun addAll(punishments: Collection<Punishment>) {
        this.punishments.addAll(punishments)
    }

    fun add(punishment: Punishment) {
        punishments.add(punishment)
    }

    fun unPunish(type: Punishment.Type, issuer: UUID, reason: String) {
        punishments.filter { punishment -> !punishment.removed && punishment.type == type }
            .forEach { punishment ->
                punishment.removed = true
                punishment.removedAt = System.currentTimeMillis()
                punishment.remover = issuer
                punishment.removeReason = reason
            }
    }
}