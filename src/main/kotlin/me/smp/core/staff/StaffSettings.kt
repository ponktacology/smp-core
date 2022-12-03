package me.smp.core.staff

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.boolean
import org.ktorm.schema.int
import org.ktorm.schema.uuid
import java.util.*

object StaffSettingsTable : Table<StaffSettings>("staff_settings") {
    val id = int("id").primaryKey().bindTo { it.id }
    val player = uuid("player").bindTo { it.player }
    val vanish = boolean("vanish").bindTo { it.vanish }
    val god = boolean("god").bindTo { it.god }
    val fly = boolean("fly").bindTo { it.fly }
}

interface StaffSettings : Entity<StaffSettings> {
    companion object : Entity.Factory<StaffSettings>()

    val id: Int
    var player: UUID
    var vanish: Boolean
    var god: Boolean
    var fly: Boolean
}
