package me.smp.core.staff

import me.smp.core.rank.RankService
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class VanishDisplayTask : Runnable, KoinComponent {

    private val rankService: RankService by inject()
    private val staffService: StaffService by inject()

    override fun run() {
        Bukkit.getOnlinePlayers().filter { rankService.getByPlayer(it).isStaff() }.forEach {
            val staffSettings = staffService.getByOnlinePlayer(it)

            if (staffSettings.vanish) {
                it.sendActionBar(Component.text("You are hidden", NamedTextColor.GREEN))
            }
        }
    }
}
