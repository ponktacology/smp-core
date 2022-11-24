package me.smp.core.vanish

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.boolean
import org.ktorm.schema.int
import org.ktorm.schema.uuid
import java.util.*

object VanishSettingsTable : Table<VanishSettings>("vanish_settings") {
    val id = int("id").primaryKey().bindTo { it.id }
    val player = uuid("player").bindTo { it.player }
    val enabled = boolean("enabled").bindTo { it.enabled }
}

interface VanishSettings : Entity<VanishSettings> {
    companion object : Entity.Factory<VanishSettings>()

    val id: Int
    var player: UUID
    var enabled: Boolean
}
