package me.smp.core.cooldown

import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CooldownService : KoinComponent {

    private val cooldownRepository: CooldownRepository by inject()

    fun isOnCooldown(player: Player, type: CooldownType) = cooldownRepository.isOnCooldown(player.uniqueId, type)

    fun reset(player: Player, type: CooldownType)  = cooldownRepository.reset(player.uniqueId, type)
}