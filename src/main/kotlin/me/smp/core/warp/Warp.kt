package me.smp.core.warp

import me.smp.core.location
import org.bukkit.Location
import org.ktorm.entity.Entity
import org.ktorm.schema.*

object Warps : Table<Warp>("warps") {
    val id = int("id").primaryKey().bindTo { it.id }
    var name = varchar("name").bindTo { it.name }
    var location = location("location").bindTo { it.location }
    var permission = varchar("permission").bindTo { it.permission }
}

interface Warp : Entity<Warp> {
    companion object : Entity.Factory<Warp>()
    val id: Int
    var name: String
    var location: Location?
    var permission: String?
}