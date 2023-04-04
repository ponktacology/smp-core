package gg.traphouse.core.pm

import gg.traphouse.core.ComponentHelper
import gg.traphouse.core.MinecraftFuture.thenAcceptMain
import gg.traphouse.core.player.PlayerContainer
import me.vaperion.blade.annotation.argument.Name
import me.vaperion.blade.annotation.argument.Sender
import me.vaperion.blade.annotation.argument.Text
import me.vaperion.blade.annotation.command.Async
import me.vaperion.blade.annotation.command.Command
import me.vaperion.blade.annotation.command.Description
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.CompletableFuture

object PrivateMessageCommands : KoinComponent {

    private val privateMessageService: PrivateMessageService by inject()

    @Command("msg", "pm", "whisper", "tell")
    @Description("Send player a private message")
    fun msg(
        @Sender sender: Player,
        @Name("player") player: Player,
        @Name("message") @Text
        message: String
    ) = privateMessageService.message(sender, player, message)

    @Command("reply", "r")
    @Description("Reply to a previously messaged player")
    fun reply(
        @Sender sender: Player,
        @Name("message") @Text
        message: String
    ) = privateMessageService.reply(sender, message)

    @Command("ignore")
    @Description("Ignore a player")
    fun ignore(@Sender sender: Player, @Name("player") player: CompletableFuture<PlayerContainer>) {
        player.thenAcceptMain {
            if (privateMessageService.isIgnoring(sender, it.uuid)) {
                sender.sendMessage("You are already ignoring this player.")
                return@thenAcceptMain
            }

            privateMessageService.ignore(sender, it.uuid)
            sender.sendMessage(ComponentHelper.createBoolean("You are", "ignoring ${it.name}.", true))
        }

    }

    @Command("unignore")
    @Description("Unignore a player")
    fun unIgnore(@Sender sender: Player, @Name("player") player: CompletableFuture<PlayerContainer>) {
        player.thenAcceptMain {
            if (!privateMessageService.isIgnoring(sender, it.uuid)) {
                sender.sendMessage("You are not ignoring this player.")
                return@thenAcceptMain
            }

            privateMessageService.unIgnore(sender, it.uuid)
            sender.sendMessage(ComponentHelper.createBoolean("You are", "ignoring ${it.name}.", false))
        }
    }

    @Command("togglepm", "tpm")
    @Description("Toggle private messages")
    fun togglepm(@Sender sender: Player) = privateMessageService.togglePrivateMessages(sender)
}
