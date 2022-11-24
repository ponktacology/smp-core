package me.smp.core.rank

import me.smp.core.nametag.NametagInfo
import me.smp.core.nametag.NametagProvider
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RankNameTagProvider : NametagProvider("Rank", 0), KoinComponent {

    private val rankService: RankService by inject()

    override fun fetchNametag(player: Player, other: Player): NametagInfo {
        val rank = rankService.getByPlayer(player)
        return createNametag(
            rank.getPrefix(),
            Component.empty(),
            rank.nameTagColor
        )
    }
}
