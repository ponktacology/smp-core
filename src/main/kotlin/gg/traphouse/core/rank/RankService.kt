package gg.traphouse.core.rank

import gg.traphouse.core.player.PlayerLookupService
import gg.traphouse.shared.network.NetworkService
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

class RankService : KoinComponent {

    private val rankRepository: RankRepository by inject()
    private val playerLookupService: PlayerLookupService by inject()
    private val networkService: NetworkService by inject()

    fun getByPlayer(player: Player) = rankRepository.getByPlayer(player)

    fun getByUUID(uuid: UUID) = rankRepository.getByUUID(uuid)

    fun grant(grant: Grant) {
        rankRepository.addGrant(grant)
        networkService.publish(PacketGrant(grant.player, grant.id))
    }

    fun removeRanks(uuid: UUID, rank: Rank, issuer: UUID, reason: String) {
        require(rank != Rank.DEFAULT) { "can't remove default rank" }
        rankRepository.removeRank(uuid, rank, issuer, reason)
        networkService.publish(PacketUngrant(uuid, rank, issuer, reason))
    }

    fun getDisplayName(player: Player) = rankRepository.getDisplayName(player)

    fun getDisplayName(uuid: UUID): Component {
        val rank = getByUUID(uuid)
        val name = playerLookupService.getNameByUUID(uuid) ?: return Component.text("Unknown", NamedTextColor.WHITE)
        return Component.text(name, NamedTextColor.GRAY, *rank.decorations)
    }

    fun getFullDisplayName(player: Player) = rankRepository.getFullDisplayName(player)
}
