package gg.traphouse.core.punishment

import gg.traphouse.shared.network.NetworkService
import gg.traphouse.shared.punishment.Punishment
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

class PunishmentService : KoinComponent {

    private val punishmentRepository: PunishmentRepository by inject()
    private val networkService: NetworkService by inject()

    fun getByPlayer(player: Player, type: Punishment.Type) = punishmentRepository.getByPlayer(player, type)

    fun getByUUID(uuid: UUID, type: Punishment.Type): Punishment? = punishmentRepository.getByUUID(uuid, type)

    fun getByUUID(uuid: UUID, address: String, type: Punishment.Type): Punishment? =
        punishmentRepository.getByUUID(uuid, address, type)

    fun punish(punishment: Punishment, silent: Boolean) {
        punishmentRepository.addPunishment(punishment)
        networkService.publish(PacketPunishment(punishment.id, silent))
    }

    // FIXME: Code duplication
    fun removePunishments(uuid: UUID, type: Punishment.Type, issuer: UUID, reason: String, silent: Boolean) {
        punishmentRepository.removePunishments(uuid, type, issuer, reason)
        networkService.publish(PacketPardon(uuid, type, issuer, reason, silent))
    }
}
