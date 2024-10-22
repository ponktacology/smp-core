package gg.traphouse.core.cooldown

class PlayerCooldowns {

    private val cooldownsById = HashMap<CooldownType, PersistentCooldown>()

    fun register(cooldown: PersistentCooldown) {
        cooldownsById.putIfAbsent(cooldown.type, cooldown)
    }

    fun isOnCooldown(type: CooldownType) = cooldownsById[type]?.hasNotExpired() ?: false

    fun cooldown(type: CooldownType): Long {
        val cooldown = cooldownsById[type] ?: return -1
        return cooldown.expiresIn()
    }

    fun reset(type: CooldownType) {
        val cooldown = cooldownsById[type] ?: throw IllegalStateException("cooldown not found")
        cooldown.reset()
    }

    fun entries() = cooldownsById.values.toList()
    override fun toString(): String {
        return "PlayerCooldowns(cooldownsById=$cooldownsById)"
    }


}