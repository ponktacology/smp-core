package me.smp.core.assistance

import me.vaperion.blade.annotation.argument.Name
import me.vaperion.blade.annotation.argument.Sender
import me.vaperion.blade.annotation.argument.Text
import me.vaperion.blade.annotation.command.Async
import me.vaperion.blade.annotation.command.Command
import me.vaperion.blade.annotation.command.Description
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object AssistanceCommands : KoinComponent {

    private val assistanceService: AssistanceService by inject()

    @Command("request")
    @Description("Request a help from staff")
    @Async
    fun request(@Sender sender: Player, @Text @Name("message") message: String) {
        if (assistanceService.isOnRequestCooldown(sender)) {
            sender.sendMessage("Wait a bit before requesting help from staff again.")
            return
        }
        assistanceService.resetRequestCooldown(sender)
        assistanceService.request(sender, message)
    }


    @Command("report")
    @Description("Request a help from staff")
    @Async
    fun report(@Sender sender: Player, @Name("player") player: Player, @Text @Name("reason") reason: String) {
        if (assistanceService.isOnReportCooldown(sender)) {
            sender.sendMessage("Wait a bit before reporting a player again.")
            return
        }
        assistanceService.resetReportCooldown(sender)
        assistanceService.report(sender, player, reason)
    }
}