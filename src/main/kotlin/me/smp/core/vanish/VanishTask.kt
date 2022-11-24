package me.smp.core.vanish

import me.smp.core.rank.RankService
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class VanishTask : Runnable, KoinComponent {

    private val rankService: RankService by inject()
    private val vanishService: VanishService by inject()

    override fun run() {
        Bukkit.getOnlinePlayers().filter { rankService.getByPlayer(it).isStaff() }.forEach {
            val vanishSettings = vanishService.getByOnlinePlayer(it)

            if (vanishSettings.enabled) {
                it.sendActionBar(Component.text("You are hidden", NamedTextColor.GREEN))
            }
        }
    }
}
