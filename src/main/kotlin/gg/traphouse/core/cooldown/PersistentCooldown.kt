package gg.traphouse.core.cooldown

import gg.traphouse.core.Cooldown

class PersistentCooldown(
    val id: Int,
    val type: CooldownType,
    startedAt: Long,
) : Cooldown(type.duration, startedAt) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PersistentCooldown

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }

    override fun toString(): String {
        return "PersistentCooldown(id=$id, type=$type)"
    }
}