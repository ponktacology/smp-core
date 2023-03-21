package me.smp.core.util

import me.smp.core.rank.RankService
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object StaffUtil : KoinComponent {

    private val rankService: RankService by inject()

    fun messageStaff(component: Component) {
        Bukkit.getOnlinePlayers().filter { rankService.getByPlayer(it).isStaff() }.forEach {
            it.sendMessage(component)
        }
    }
}
