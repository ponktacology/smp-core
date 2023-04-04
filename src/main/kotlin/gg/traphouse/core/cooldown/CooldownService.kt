package gg.traphouse.core.cooldown

import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CooldownService : KoinComponent {

    private val cooldownRepository: CooldownRepository by inject()

    fun hasCooldown(player: Player, type: CooldownType) = cooldownRepository.isOnCooldown(player, type)

    fun reset(player: Player, type: CooldownType) = cooldownRepository.reset(player, type)
}
