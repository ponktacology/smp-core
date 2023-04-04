package gg.traphouse.core.staff.assistance

import gg.traphouse.core.TaskDispatcher
import gg.traphouse.core.cooldown.CooldownService
import gg.traphouse.core.cooldown.CoreCooldowns
import me.vaperion.blade.annotation.argument.Name
import me.vaperion.blade.annotation.argument.Sender
import me.vaperion.blade.annotation.argument.Text
import me.vaperion.blade.annotation.command.Async
import me.vaperion.blade.annotation.command.Command
import me.vaperion.blade.annotation.command.Description
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object AssistanceCommands : KoinComponent {

    private val assistanceService: AssistanceService by inject()
    private val cooldownService: CooldownService by inject()

    @Command("request", "helpop")
    @Description("Request help from staff")
    fun request(
        @Sender sender: Player,
        @Text
        @Name("message")
        message: String
    ) {
        if (cooldownService.hasCooldown(sender, CoreCooldowns.ASSISTANCE_REQUEST)) {
            sender.sendMessage(
                Component.text(
                    "Wait a bit before requesting help from staff again.",
                    NamedTextColor.RED
                )
            )
            return
        }
        cooldownService.reset(sender, CoreCooldowns.ASSISTANCE_REQUEST)
        assistanceService.request(sender, message)
    }

    @Command("report")
    @Description("Report a player")
    fun report(
        @Sender sender: Player,
        @Name("player") player: Player,
        @Name("reason")
        @Text
        reason: String
    ) {
        if (cooldownService.hasCooldown(sender, CoreCooldowns.ASSISTANCE_REPORT)) {
            sender.sendMessage(Component.text("Wait a bit before reporting a player again.", NamedTextColor.RED))
            return
        }
        cooldownService.reset(sender, CoreCooldowns.ASSISTANCE_REPORT)
        assistanceService.report(sender, player, reason)
    }
}
