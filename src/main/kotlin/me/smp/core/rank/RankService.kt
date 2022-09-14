package me.smp.core.rank

import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.UUID

class RankService : KoinComponent {

    private val rankRepository: RankRepository by inject()

    fun getByOnlinePlayer(player: Player) = rankRepository.getByOnlinePlayer(player)

    fun getByUUID(uuid: UUID) = rankRepository.getByUUID(uuid)

    fun grant(grant: Grant): Boolean {
        require(grant.rank != Rank.DEFAULT) { "can't grant default rank" }
        return rankRepository.grantRank(grant.player, grant) == 1
    }

    fun removeRanks(uuid: UUID, rank: Rank, issuer: UUID, reason: String) {
        require(rank != Rank.DEFAULT) { "can't remove default rank" }
        rankRepository.removeRank(uuid, rank, issuer, reason)
    }
}