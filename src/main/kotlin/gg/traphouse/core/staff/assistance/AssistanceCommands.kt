package gg.traphouse.core.staff.assistance

import gg.traphouse.core.cooldown.CooldownService
import gg.traphouse.core.cooldown.CoreCooldowns
import gg.traphouse.core.util.SenderUtil.sendOnCooldown
import me.vaperion.blade.annotation.argument.Name
import me.vaperion.blade.annotation.argument.Sender
import me.vaperion.blade.annotation.argument.Text
import me.vaperion.blade.annotation.command.Command
import me.vaperion.blade.annotation.command.Description
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object AssistanceCommands : KoinComponent {

    private val assistanceService: AssistanceService by inject()
    private val cooldownService: CooldownService by inject()

    @Command("request", "helpop")
    @Description("Wysyła prośbę o pomoc do administracji")
    fun request(
        @Sender sender: Player,
        @Text
        @Name("message")
        message: String
    ) {
        if (cooldownService.hasCooldown(sender, CoreCooldowns.ASSISTANCE_REQUEST)) {
            sender.sendOnCooldown("Odczekaj chwilę zanim znowu wyślesz prośbę do administracji.")
            return
        }

        cooldownService.reset(sender, CoreCooldowns.ASSISTANCE_REQUEST)
        assistanceService.request(sender, message)
    }

    @Command("report", "zglos")
    @Description("Zgłasza gracza do administracji")
    fun report(
        @Sender sender: Player,
        @Name("player") player: Player,
        @Name("reason")
        @Text
        reason: String
    ) {
        if (cooldownService.hasCooldown(sender, CoreCooldowns.ASSISTANCE_REPORT)) {
            sender.sendOnCooldown("Odczekaj chwilę zanim znowu zgłosisz gracza.")
            return
        }

        cooldownService.reset(sender, CoreCooldowns.ASSISTANCE_REPORT)
        assistanceService.report(sender, player, reason)
    }
}
