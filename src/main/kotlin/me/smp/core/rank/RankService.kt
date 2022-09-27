package me.smp.core.rank

import me.smp.core.name.NameService
import me.smp.core.network.NetworkService
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

class RankService : KoinComponent {

    private val rankRepository: RankRepository by inject()
    private val nameService: NameService by inject()
    private val networkService: NetworkService by inject()

    fun getByPlayer(player: Player) = rankRepository.getByPlayer(player)

    fun getByUUID(uuid: UUID) = rankRepository.getByUUID(uuid)

    fun grant(grant: Grant) {
        rankRepository.addGrant(grant)
        networkService.publish(PacketGrant(grant.player, grant.rank))
    }

    fun removeRanks(uuid: UUID, rank: Rank, issuer: UUID, reason: String) {
        require(rank != Rank.DEFAULT) { "can't remove default rank" }
        rankRepository.removeRank(uuid, rank, issuer, reason)
        networkService.publish(PacketUngrant(uuid, rank))
    }

    fun getDisplayName(player: Player): Component {
        val rank = getByPlayer(player)
        return Component.text(player.name, rank.color, *rank.decorations)
    }

    fun getDisplayName(uuid: UUID): Component {
        val rank = getByUUID(uuid)
        val name = nameService.getByUUID(uuid) ?: return Component.text("Anonymous", NamedTextColor.WHITE)
        return Component.text(name, rank.color, *rank.decorations)
    }

    fun getFullDisplayName(player: Player): Component {
        val rank = getByPlayer(player)
        val prefix = rank.getPrefix()
        return prefix.append(getDisplayName(player))
    }
}