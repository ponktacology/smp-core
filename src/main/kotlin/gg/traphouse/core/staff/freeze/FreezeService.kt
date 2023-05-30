package gg.traphouse.core.staff.freeze

import gg.traphouse.core.Task
import gg.traphouse.core.nametag.NameTagHandler
import gg.traphouse.shared.network.NetworkService
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
        NameTagHandler.reloadPlayer(player)
        Task.async { networkService.publish(PacketFreeze(issuer, player.uniqueId)) }
    }

    fun unFreeze(player: Player) {
        freezeRepository.unFreeze(player)
        NameTagHandler.reloadPlayer(player)
        player.openInventory.close()
    }

    fun loggedOutWhileFrozen(player: Player) {
        Task.async { networkService.publish(PacketFreezeLogout(player.uniqueId)) }
    }
}
