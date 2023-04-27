package gg.traphouse.core.nametag

import gg.traphouse.core.rank.RankService
import gg.traphouse.core.staff.StaffService
import gg.traphouse.core.staff.freeze.FreezeService
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

    override fun modifyNameTag(previous: NametagInfo, viewer: Player, viewed: Player): NametagInfo {
        val rank = rankService.getByPlayer(viewer)
        val otherRank = rankService.getByPlayer(viewed)
        val staffSettings = staffService.getByOnlinePlayer(viewer)
        val suffix = if (staffSettings.vanish && otherRank.isStaff()) {
            Component.text(" HIDDEN", NamedTextColor.GREEN, TextDecoration.BOLD)
        } else if (freezeService.isFrozen(viewer)) {
            Component.text(" FROZEN", NamedTextColor.DARK_RED, TextDecoration.BOLD)
        } else Component.empty()

        val prefix = rank.getPrefix()

        return NametagInfo(
            if (prefix == Component.empty()) prefix else prefix.append(Component.text(" ")),
            suffix,
            rank.nameTagColor
        )
    }
}