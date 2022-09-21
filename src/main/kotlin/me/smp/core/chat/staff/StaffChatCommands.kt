package me.smp.core.chat.staff

import me.vaperion.blade.annotation.argument.Name
import me.vaperion.blade.annotation.argument.Sender
import me.vaperion.blade.annotation.argument.Text
import me.vaperion.blade.annotation.command.Async
import me.vaperion.blade.annotation.command.Command
import me.vaperion.blade.annotation.command.Permission
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object StaffChatCommands : KoinComponent {

    private val staffChatService: StaffChatService by inject()

    @Command("staffchat", "sc")
    @Permission("core.staffchat")
    @Async
    fun staffchat(@Sender sender: Player, @Text @Name("message") message: String) = staffChatService.message(sender, message)
}