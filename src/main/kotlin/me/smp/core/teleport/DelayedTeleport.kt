package me.smp.core.teleport

import me.smp.core.Plugin
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.scheduler.BukkitRunnable
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.UUID

class DelayedTeleport(
    private val player: UUID,
    private val location: Location,
    private val delayInSeconds: Int,
    val cleanUp: (UUID) -> Unit = {}
) : BukkitRunnable(), KoinComponent {

    private val plugin: Plugin by inject()

    init {
        this.runTaskTimer(plugin, 0L, 20L)
    }

    private var passedSeconds = 0

    override fun cancel() {
        super.cancel()
        cleanUp(player)
    }

    override fun run() {
        player() ?: run {
            cancel()
            return
        }

        if (passedSeconds >= delayInSeconds) {
            player()?.teleport(location)
            player()?.let {
                it.teleport(location)
                it.sendActionBar(Component.text("Teleported", NamedTextColor.GREEN))
            }
            cancel()
        } else player()?.sendActionBar(Component.text("Teleporting in ${delayInSeconds - passedSeconds}s"))

        passedSeconds++
    }

    private fun player() = Bukkit.getPlayer(player)
}