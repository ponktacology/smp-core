package gg.traphouse.core.punishment

import gg.traphouse.core.Config
import gg.traphouse.core.TaskDispatcher
import gg.traphouse.core.rank.RankService
import gg.traphouse.core.util.StaffUtil
import gg.traphouse.shared.TimeFormatter
import gg.traphouse.shared.network.NetworkHandler
import gg.traphouse.shared.network.NetworkListener
import gg.traphouse.shared.punishment.Punishment
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

class PunishmentListener : KoinComponent, Listener, NetworkListener {

    private val punishmentService: PunishmentService by inject()
    private val punishmentRepository: PunishmentRepository by inject()
    private val rankService: RankService by inject()

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    fun onPlayerChatMessage(event: AsyncChatEvent) {
        punishmentService.getByPlayer(event.player, Punishment.Type.MUTE)?.let {
            event.player.sendMessage(Component.text("You can't use chat while muted!", NamedTextColor.RED))
            event.isCancelled = true
        }
    }

    @NetworkHandler
    fun onPunishment(packet: PacketPunishment) {
        val punishment = punishmentRepository.getById(packet.punishmentId) ?: error("invalid punishment")
        Bukkit.getPlayer(punishment.player)?.let {
            TaskDispatcher.dispatch {
                val permanent = punishment.duration.isPermanent()
                var component = Component.newline()
                    .append(
                        if (punishment.type != Punishment.Type.KICK) Component.text(
                            "You have been${if (permanent) " permanently " else " "}${punishment.type.addFormat}!",
                            NamedTextColor.RED,
                            TextDecoration.ITALIC
                        ).append(Component.newline())
                        else Component.empty()
                    )
                    .append(Component.newline())
                    .append(Component.text("Reason: ${punishment.reason}", NamedTextColor.RED))

                if (punishment.type != Punishment.Type.KICK) {
                    if (!permanent) {
                        component = component.append(
                            Component.newline()
                                .append(
                                    Component.text(
                                        "Expires at: ${TimeFormatter.formatDate(punishment.duration + punishment.addedAt)}",
                                        NamedTextColor.RED
                                    )
                                )
                        )
                    }
                    component =
                        component.append(
                            Component.newline()
                                .append(Component.newline())
                                .append(
                                    Component.text(
                                        "You can appeal at discord dc.traphouse.gg",
                                        NamedTextColor.RED
                                    )
                                )
                        )
                }

                if (punishment.type == Punishment.Type.MUTE) {
                    it.sendMessage(component)
                } else it.kick(component)
            }
        }

        punishmentRepository.punish(punishment)

        announcePunishment(
            punishment.duration.isPermanent(),
            punishment.player,
            punishment.type,
            punishment.issuer,
            punishment.reason,
            true,
            packet.silent
        )
    }

    @NetworkHandler
    fun onPardon(packet: PacketPardon) {
        punishmentRepository.unPunish(packet.player, packet.type, packet.issuer, packet.reason)
        announcePunishment(
            false,
            packet.player,
            packet.type,
            packet.issuer,
            packet.reason,
            false,
            packet.silent
        )
    }

    private fun announcePunishment(
        permanent: Boolean,
        player: UUID,
        type: Punishment.Type,
        issuer: UUID,
        reason: String,
        add: Boolean,
        silent: Boolean
    ) {
        var component = Component.text("")
            .append(Component.text("Player ", NamedTextColor.RED))
            .append(rankService.getDisplayName(player))
            .append(Component.text(" has been", NamedTextColor.RED))

        if (permanent) {
            component = component.append(Component.text(" permanently", NamedTextColor.RED))
        }

        component = component.append(Component.text(" "))
            .append(Component.text(if (add) type.addFormat else type.removeFormat, NamedTextColor.RED))
            .append(Component.text(" by ", NamedTextColor.RED))
            .append(rankService.getDisplayName(issuer))
            .hoverEvent(HoverEvent.showText(Component.text(reason)))

        if (silent) {
            component = Component.text("[Silent] ", NamedTextColor.GRAY)
                .append(Component.text("[${Config.SERVER_NAME}] ", NamedTextColor.BLUE))
                .append(component)

            StaffUtil.messageStaff(component)
        } else Bukkit.broadcast(component)
    }
}