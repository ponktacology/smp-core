package me.smp.core.cooldown

import me.smp.core.Cooldown

class PersistentCooldown(
    val id: Int,
    val type: CooldownType,
    startedAt: Long,
) : Cooldown(type.duration, startedAt)