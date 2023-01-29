package me.smp.core.staff.assistance

import me.smp.core.TaskDispatcher
import me.smp.shared.network.NetworkService
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AssistanceService : KoinComponent {

    private val networkService: NetworkService by inject()

    fun report(issuer: Player, player: Player, reason: String) = TaskDispatcher.dispatchAsync {
        networkService.publish(PacketReportPlayer(player.uniqueId, issuer.uniqueId, reason))
    }

    fun request(issuer: Player, reason: String) =
        TaskDispatcher.dispatchAsync { networkService.publish(PacketPlayerRequest(issuer.uniqueId, reason)) }
}
