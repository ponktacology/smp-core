package me.smp.core.staff.assistance

import me.smp.core.cooldown.CooldownService
import me.smp.core.cooldown.Cooldowns
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
    private val cooldownService: CooldownService by inject()

    @Command("request", "helpop")
    @Description("Request a help from staff")
    @Async
    fun request(
        @Sender sender: Player,
        @Text
        @Name("message")
        message: String
    ) {
        if (cooldownService.isOnCooldown(sender, Cooldowns.ASSISTANCE_REQUEST)) {
            sender.sendMessage("Wait a bit before requesting help from staff again.")
            return
        }
        cooldownService.reset(sender, Cooldowns.ASSISTANCE_REQUEST)
        assistanceService.request(sender, message)
    }

    @Command("report")
    @Description("Request a help from staff")
    @Async
    fun report(
        @Sender sender: Player,
        @Name("player") player: Player,
        @Name("reason")
        @Text
        reason: String
    ) {
        if (cooldownService.isOnCooldown(sender, Cooldowns.ASSISTANCE_REPORT)) {
            sender.sendMessage("Wait a bit before reporting a player again.")
            return
        }
        cooldownService.reset(sender, Cooldowns.ASSISTANCE_REPORT)
        assistanceService.report(sender, player, reason)
    }
}
