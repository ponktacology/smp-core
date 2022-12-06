package me.smp.core.staff

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class VanishDisplayTask : Runnable, KoinComponent {

    private val staffService: StaffService by inject()

    override fun run() {
        Bukkit.getOnlinePlayers().forEach {
            val staffSettings = staffService.getByOnlinePlayer(it)

            if (staffSettings.vanish) {
                it.sendActionBar(Component.text("HIDDEN", NamedTextColor.GREEN, TextDecoration.BOLD))
            }
        }
    }
}
