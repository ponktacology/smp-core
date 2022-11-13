package me.smp.core.chat

import me.vaperion.blade.annotation.argument.Name
import me.vaperion.blade.annotation.argument.Sender
import me.vaperion.blade.annotation.command.Command
import me.vaperion.blade.annotation.command.Permission
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
}
