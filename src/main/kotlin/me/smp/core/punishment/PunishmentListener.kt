package me.smp.core.punishment


import io.papermc.paper.event.player.AsyncChatEvent
import me.smp.core.Config
import me.smp.core.TimeFormatter
import me.smp.core.TaskDispatcher
import me.smp.core.network.NetworkHandler
import me.smp.core.network.NetworkListener
import me.smp.core.rank.RankService
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

class PunishmentListener : KoinComponent, Listener, NetworkListener {

    private val logger: Logger by inject()
    private val punishmentService: PunishmentService by inject()
    private val punishmentRepository: PunishmentRepository by inject()
    private val rankService: RankService by inject()

    @EventHandler
    fun onProfileLogin(event: AsyncPlayerPreLoginEvent) {
        punishmentService.getByUUID(event.uniqueId, event.address.toString(), Punishment.Type.BAN)?.let {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, Component.text("Banned :(!"))
            logger.log(Level.INFO, "Player ${event.name} tried to join but is banned.")
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    fun onPlayerChatMessage(event: AsyncChatEvent) {
        punishmentService.getByPlayer(event.player, Punishment.Type.MUTE)?.let {
            event.player.sendMessage("You can't use chat while muted!")
            event.isCancelled = true
        }
    }

    @NetworkHandler
    fun onPunishment(packet: PacketPunishment) {
        val punishment = punishmentRepository.getById(packet.punishmentId) ?: error("invalid punishment")
        if (punishment.type != Punishment.Type.MUTE) {
            Bukkit.getPlayer(punishment.player)?.let {
                TaskDispatcher.dispatch {
                    val permanent = punishment.duration.isPermanent()
                    val component = Component.text(
                        "You have been${if (permanent) " permanently " else " "}${punishment.type.addFormat}!",
                        NamedTextColor.RED,
                        TextDecoration.ITALIC
                    )
                        .append(Component.newline())
                        .append(Component.newline())
                        .append(Component.text("Reason: ${punishment.reason}", NamedTextColor.RED))

                    if (!permanent) {
                        component.append(
                            Component.newline()
                                .append(Component.text("Expires: ${TimeFormatter.formatDate(punishment.duration + punishment.addedAt)}"))
                        )
                    }

                    it.kick(component)
                }
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

            Bukkit.getOnlinePlayers().forEach {
                val rank = rankService.getByPlayer(it)
                if (rank.isStaff()) {
                    it.sendMessage(component)
                }
            }
        } else Bukkit.broadcast(component)
    }

}