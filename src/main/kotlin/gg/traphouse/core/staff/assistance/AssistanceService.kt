package gg.traphouse.core.staff.assistance

import gg.traphouse.core.Task
import gg.traphouse.shared.network.NetworkService
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AssistanceService : KoinComponent {

    private val networkService: NetworkService by inject()

    fun report(issuer: Player, player: Player, reason: String) = Task.async {
        networkService.publish(PacketReportPlayer(player.uniqueId, issuer.uniqueId, reason))
    }

    fun request(issuer: Player, reason: String) =
        Task.async { networkService.publish(PacketPlayerRequest(issuer.uniqueId, reason)) }
}
