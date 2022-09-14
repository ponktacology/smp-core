package me.smp.core.name

import me.smp.core.Console
import org.bukkit.Bukkit
import java.util.UUID

//TODO: Add redis caching
class NameService {

    fun getByUUID(uuid: UUID): String? {
        return if(uuid == Console.UUID) Console.DISPLAY_NAME else Bukkit.getPlayer(uuid)?.name
    }

    fun getByName(name: String): UUID? {
        return Bukkit.getPlayer(name)?.uniqueId
    }
}