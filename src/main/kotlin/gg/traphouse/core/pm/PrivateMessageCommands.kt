package gg.traphouse.core.pm

import gg.traphouse.core.player.PlayerContainer
import gg.traphouse.core.util.SenderUtil.sendError
import gg.traphouse.core.util.SenderUtil.sendSuccess
import me.vaperion.blade.annotation.argument.Name
import me.vaperion.blade.annotation.argument.Sender
import me.vaperion.blade.annotation.argument.Text
import me.vaperion.blade.annotation.command.Async
import me.vaperion.blade.annotation.command.Command
import me.vaperion.blade.annotation.command.Description
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object PrivateMessageCommands : KoinComponent {

    private val privateMessageService: PrivateMessageService by inject()

    @Command("msg", "pm", "whisper", "tell")
    @Description("Wysyła prywatną wiadomość do gracza")
    fun msg(
        @Sender sender: Player,
        @Name("player") player: Player,
        @Name("message") @Text
        message: String
    ) = privateMessageService.message(sender, player, message)

    @Command("reply", "r", "odpisz")
    @Description("Odpisuje na prywatną wiadomość od gracza")
    fun reply(
        @Sender sender: Player,
        @Name("message") @Text
        message: String
    ) = privateMessageService.reply(sender, message)

    @Command("ignore", "ignoruj")
    @Description("Ignoruje prywatne wiadomości od gracza")
    @Async
    fun ignore(@Sender sender: Player, @Name("player") player: PlayerContainer) {
        if (sender.uniqueId == player.uuid) {
            sender.sendError("Nie możesz ignorować samego siebie.")
            return
        }

        if (privateMessageService.isIgnoring(sender, player.uuid)) {
            sender.sendError("Już ignorujesz tego gracza.")
            return
        }

        privateMessageService.ignore(sender, player.uuid)
        sender.sendSuccess("Od teraz ignorujesz wszystkie prywatne wiadomości od ${player.name}.")
    }

    @Command("unignore", "odignoruj")
    @Description("Przestaje ignorować prywatne wiadomości od gracza")
    @Async
    fun unIgnore(@Sender sender: Player, @Name("player") player: PlayerContainer) {
        if (sender.uniqueId == player.uuid) {
            sender.sendError("Nie możesz ignorować samego siebie.")
            return
        }

        if (!privateMessageService.isIgnoring(sender, player.uuid)) {
            sender.sendError("Nie ignorujesz tego gracza.")
            return
        }

        privateMessageService.unIgnore(sender, player.uuid)
        sender.sendSuccess("Przestałeś ignorować prywatne wiadomości od ${player.name}.")
    }

    @Command("tpm", "wyjebane")
    @Description("Zmienia status ignorowania wszystkich prywatnych wiadomości")
    fun togglepm(@Sender sender: Player) = privateMessageService.togglePrivateMessages(sender)
}
