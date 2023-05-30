package gg.traphouse.core.pm

import gg.traphouse.core.ComponentHelper.sendStateComponent
import gg.traphouse.core.rank.RankService
import gg.traphouse.core.util.SenderUtil.sendError
import gg.traphouse.core.util.StaffUtil.isStaff
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

class PrivateMessageService : KoinComponent {

    private val privateMessageRepository: PrivateMessageRepository by inject()
    private val rankService: RankService by inject()

    fun reply(sender: Player, message: String) {
        val replier = getReplier(sender) ?: run {
            sender.sendError("Nie masz komu odpisać.")
            return
        }

        message(sender, replier, message)
    }

    fun message(sender: Player, receiver: Player, message: String) {
        if (sender == receiver) {
            sender.sendError("Nie możesz wysłać prywatnej wiadomości do samego siebie.")
            return
        }

        if (!sender.isStaff()) {
            if (hasDisabledPrivateMessages(sender)) {
                sender.sendError("Nie możesz wysyłać wiadomości, gdyż masz je wyłączone. Użyj komendy /tpm, aby to zmienić.")
                return
            }

            if (isIgnoring(sender, receiver.uniqueId)) {
                sender.sendError("Nie możesz wysyłać wiadomości do gracza, którego ignorujesz. Przestań go ignorować komendą /odignoruj ${receiver.name}")
                return
            }

            if (hasDisabledPrivateMessages(receiver) || isIgnoring(receiver, sender.uniqueId)) {
                sender.sendError("Gracz ma wyłączone prywatne wiadomości.")
                return
            }
        }

        privateMessageRepository.setReplier(sender, receiver)
        privateMessageRepository.setReplier(receiver, sender)

        sender.sendMessage(
            Component.empty()
                .append(Component.text("(Od ", NamedTextColor.AQUA))
                .append(rankService.getDisplayName(receiver))
                .append(Component.text(") ", NamedTextColor.AQUA))
                .append(Component.text(message, NamedTextColor.WHITE))
        )
        receiver.sendMessage(
            Component.empty()
                .append(Component.text("(Do ", NamedTextColor.AQUA))
                .append(rankService.getDisplayName(sender))
                .append(Component.text(") ", NamedTextColor.AQUA))
                .append(Component.text(message, NamedTextColor.WHITE))
        )
    }

    private fun getReplier(player: Player) = privateMessageRepository.getReplier(player)?.let {
        Bukkit.getPlayer(it)
    }

    private fun hasDisabledPrivateMessages(player: Player) =
        !privateMessageRepository.settingsByPlayer(player).enabled

    fun togglePrivateMessages(player: Player) {
        val settings = privateMessageRepository.settingsByPlayer(player)
        settings.enabled = !settings.enabled
        player.sendStateComponent("Tryb ignorowania prywatnych wiadomości został %s.", !settings.enabled)
    }

    fun ignore(player: Player, uuid: UUID) = privateMessageRepository.ignore(player, uuid)

    fun unIgnore(player: Player, uuid: UUID) = privateMessageRepository.unIgnore(player, uuid)

    fun isIgnoring(player: Player, other: UUID) = privateMessageRepository.isIgnoring(player, other)
}
