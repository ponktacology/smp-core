package me.smp.core.staff.freeze

import me.smp.core.TaskDispatcher
import me.smp.core.nametag.FrozenNametagHandler
import me.smp.shared.network.NetworkService
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

class FreezeService : KoinComponent {

    private val freezeRepository: FreezeRepository by inject()
    private val networkService: NetworkService by inject()

    fun isFrozen(player: Player) = freezeRepository.isFrozen(player)

    fun freeze(issuer: UUID, player: Player) {
        freezeRepository.freeze(player)
        FreezeGUI().open(player)
        FrozenNametagHandler.reloadPlayer(player)
        TaskDispatcher.dispatchAsync { networkService.publish(PacketFreeze(issuer, player.uniqueId)) }
    }

    fun unFreeze(player: Player) {
        freezeRepository.unFreeze(player)
        FrozenNametagHandler.reloadPlayer(player)
        player.closeInventory()
    }

    fun loggedOutWhileFrozen(player: Player) {
        TaskDispatcher.dispatchAsync { networkService.publish(PacketFreezeLogout(player.uniqueId)) }
    }
}
