package me.smp.core.punishment

import me.smp.core.TaskDispatcher
import me.smp.core.name.NameService
import me.smp.core.rank.RankService
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

class PunishmentService : KoinComponent {

    private val punishmentRepository: PunishmentRepository by inject()
    private val nameService: NameService by inject()
    private val rankService: RankService by inject()

    fun getByPlayer(player: Player, type: Punishment.Type) = punishmentRepository.getByPlayer(player, type)

    fun getByUUID(uuid: UUID, type: Punishment.Type): Punishment? = punishmentRepository.getByUUID(uuid, type)

    fun punish(punishment: Punishment, silent: Boolean) {
        punishmentRepository.punish(punishment)

        if (punishment.type != Punishment.Type.MUTE) {
            Bukkit.getPlayer(punishment.player)?.let {
                TaskDispatcher.dispatch { it.kick(Component.text(punishment.reason, NamedTextColor.RED)) }
            }
        }

        var component = Component.text("")

        if (silent) {
            component = component.append(Component.text("[Silent] ", NamedTextColor.GRAY))
        }

        component = component.append(Component.text("Player ", NamedTextColor.RED))
            .append(rankService.getDisplayName(punishment.player))
            .append(Component.text(" has been", NamedTextColor.RED))

        if (punishment.duration.isPermanent()) {
            component = component.append(Component.text(" permanently", NamedTextColor.RED))
        }

        component = component.append(Component.text(" "))
            .append(Component.text(punishment.type.addFormat, NamedTextColor.RED))
            .append(Component.text(" by ", NamedTextColor.RED))
            .append(rankService.getDisplayName(punishment.issuer))
            .hoverEvent(HoverEvent.showText(Component.text(punishment.reason)))

        if (silent) {
            for (player in Bukkit.getOnlinePlayers()) {
                val rank = rankService.getByPlayer(player)
                if (rank.isStaff()) {
                    player.sendMessage(component)
                }
            }
        } else Bukkit.broadcast(component)
    }

    //FIXME: Code duplication
    fun removePunishments(uuid: UUID, type: Punishment.Type, issuer: UUID, reason: String, silent: Boolean) {
        punishmentRepository.removePunishments(uuid, type, issuer, reason)

        val issuerName = nameService.getByUUID(issuer)
            ?: error("Couldn't fetch issuer's name $issuer")
        val issuerRank = rankService.getByUUID(issuer)
        val playerName = nameService.getByUUID(uuid)
            ?: error("Couldn't fetch punished player's name $uuid")
        val rank = rankService.getByUUID(uuid)

        var component = Component.text("")

        if (silent) {
            component = component.append(Component.text("[Silent] ", NamedTextColor.GRAY))
        }

        component = component.append(Component.text("Player ", NamedTextColor.RED))
            .append(Component.text(playerName, rank.color))
            .append(Component.text(" has been", NamedTextColor.RED))

        component = component.append(Component.text(" "))
            .append(Component.text(type.removeFormat, NamedTextColor.RED))
            .append(Component.text(" by ", NamedTextColor.RED))
            .append(Component.text(issuerName, issuerRank.color))
            .hoverEvent(HoverEvent.showText(Component.text(reason)))

        if (silent) {
            for (player in Bukkit.getOnlinePlayers()) {
                val rank = rankService.getByPlayer(player)
                if (rank.isStaff()) {
                    player.sendMessage(component)
                }
            }
        } else Bukkit.broadcast(component)
    }
}