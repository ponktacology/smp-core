package gg.traphouse.core.util

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.permissions.Permissible
import org.koin.core.component.KoinComponent

object StaffUtil : KoinComponent {

    fun Permissible.isStaff() = this.hasPermission("staff")

    fun messageStaff(component: Component) {
        Bukkit.getOnlinePlayers().filter { it.isStaff() }.forEach {
            it.sendMessage(component)
        }
    }
}
