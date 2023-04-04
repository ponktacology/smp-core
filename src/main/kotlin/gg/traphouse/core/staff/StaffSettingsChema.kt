package gg.traphouse.core.staff

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.boolean
import org.ktorm.schema.int
import org.ktorm.schema.uuid
import java.util.*

fun StaffSettings.toRemote() = RemoteStaffSettings {
    this.id = this@toRemote.id
    this.player = this@toRemote.player
    this.vanish = this@toRemote.vanish
    this.god = this@toRemote.god
    this.fly = this@toRemote.fly
}

fun RemoteStaffSettings.toDomain() = StaffSettings(this.id, this.player, this.vanish, this.god, this.fly)

object StaffSettingsTable : Table<RemoteStaffSettings>("staff_settings") {
    val id = int("id").primaryKey().bindTo { it.id }
    val player = uuid("player").bindTo { it.player }
    val vanish = boolean("vanish").bindTo { it.vanish }
    val god = boolean("god").bindTo { it.god }
    val fly = boolean("fly").bindTo { it.fly }
}

interface RemoteStaffSettings : Entity<RemoteStaffSettings> {
    companion object : Entity.Factory<RemoteStaffSettings>()

    var id: Int
    var player: UUID
    var vanish: Boolean
    var god: Boolean
    var fly: Boolean
}
