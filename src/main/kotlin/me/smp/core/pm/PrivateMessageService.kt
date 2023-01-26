package me.smp.core.pm

import me.smp.core.ComponentHelper
import me.smp.core.SyncCatcher
import me.smp.core.rank.RankService
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
            sender.sendMessage("You don't have anyone to reply to.")
            return
        }

        message(sender, replier, message)
    }

    fun message(sender: Player, receiver: Player, message: String) {
        if (!sender.hasPermission("pm.bypassignore")) {
            if (hasDisabledPrivateMessages(sender)) {
                sender.sendMessage("Your private messages are currently disabled. Enable them by using /togglepm.")
                return
            }

            if (isIgnoring(sender, receiver.uniqueId)) {
                sender.sendMessage("You can't send message to the player you are ignoring. Unignore them using /unignore ${receiver.name}")
                return
            }

            if (hasDisabledPrivateMessages(receiver) || isIgnoring(receiver, sender.uniqueId)) {
                sender.sendMessage("This player has disabled private messages.")
                return
            }
        }

        privateMessageRepository.setReplier(sender, receiver)
        privateMessageRepository.setReplier(receiver, sender)

        sender.sendMessage(
            Component.empty()
                .append(Component.text("(To ", NamedTextColor.AQUA))
                .append(rankService.getDisplayName(receiver))
                .append(Component.text(") ", NamedTextColor.AQUA))
                .append(Component.text(message, NamedTextColor.WHITE))
        )
        receiver.sendMessage(
            Component.empty()
                .append(Component.text("(From ", NamedTextColor.AQUA))
                .append(rankService.getDisplayName(sender))
                .append(Component.text(") ", NamedTextColor.AQUA))
                .append(Component.text(message, NamedTextColor.WHITE))
        )
    }

    private fun getReplier(player: Player) = privateMessageRepository.getReplier(player)?.let {
        Bukkit.getPlayer(it)
    }

    private fun hasDisabledPrivateMessages(player: Player) =
        !privateMessageRepository.getSettingsByPlayer(player).enabled

    fun togglePrivateMessages(player: Player) {
        SyncCatcher.verify()
        val settings = privateMessageRepository.getSettingsByPlayer(player)
        settings.enabled = !settings.enabled
        player.sendMessage(ComponentHelper.createBoolean("Your private messages are", "enabled.", settings.enabled))
    }

    fun ignore(player: Player, uuid: UUID) = privateMessageRepository.ignore(player, uuid)

    fun unIgnore(player: Player, uuid: UUID) = privateMessageRepository.unIgnore(player, uuid)

    fun isIgnoring(player: Player, other: UUID) = privateMessageRepository.isIgnoring(player, other)
}
