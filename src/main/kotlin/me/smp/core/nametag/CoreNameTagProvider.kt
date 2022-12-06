package me.smp.core.nametag

import me.smp.core.rank.RankService
import me.smp.core.staff.StaffService
import me.smp.core.staff.freeze.FreezeService
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CoreNameTagProvider : NametagProvider("Rank", 0), KoinComponent {

    private val rankService: RankService by inject()
    private val staffService: StaffService by inject()
    private val freezeService: FreezeService by inject()

    override fun fetchNametag(player: Player, other: Player): NametagInfo {
        val rank = rankService.getByPlayer(player)
        val otherRank = rankService.getByPlayer(other)
        val staffSettings = staffService.getByOnlinePlayer(player)
        val suffix = if (staffSettings.vanish && otherRank.isStaff()) {
            Component.text(" HIDDEN", NamedTextColor.GREEN, TextDecoration.BOLD)
        } else if (freezeService.isFrozen(player)) {
            Component.text(" FROZEN", NamedTextColor.DARK_RED, TextDecoration.BOLD)
        } else Component.empty()
        return createNametag(
            rank.getPrefix(),
            suffix,
            rank.nameTagColor
        )
    }
}