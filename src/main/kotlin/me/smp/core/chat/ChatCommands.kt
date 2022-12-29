package me.smp.core.chat

import me.vaperion.blade.annotation.argument.Flag
import me.vaperion.blade.annotation.argument.Name
import me.vaperion.blade.annotation.argument.Sender
import me.vaperion.blade.annotation.argument.Text
import me.vaperion.blade.annotation.command.Command
import me.vaperion.blade.annotation.command.Permission
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.command.CommandSender
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object ChatCommands : KoinComponent {

    private val chatService: ChatService by inject()

    @Command("chat state")
    @Permission("core.chat.state")
    fun state(@Sender sender: CommandSender, @Name("state") state: ChatState) {
        if (chatService.chatState() == state) {
            sender.sendMessage("This state is already set.")
            return
        }
        chatService.updateState(state)
    }

    @Command("broadcast")
    @Permission("core.chat.broadcast")
    fun broadcast(
        @Sender sender: CommandSender,
        @Flag(value = 'r', description = "raw broadcast") raw: Boolean,
        @Text @Name("message") message: String
    ) {
        chatService.broadcast(MiniMessage.get().parse(message), raw)
    }
}
