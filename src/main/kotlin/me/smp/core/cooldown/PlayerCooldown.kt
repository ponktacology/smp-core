package me.smp.core.cooldown

import org.ktorm.entity.Entity
import org.ktorm.schema.*
import java.util.UUID

object PlayerCooldowns : Table<PlayerCooldown>("cooldowns") {
    val id = varchar("id").primaryKey().bindTo { it.id }
    val player = uuid("player").bindTo { it.player }
    val resetAt = long("reset_at").bindTo { it.resetAt }
}

interface PlayerCooldown : Entity<PlayerCooldown> {
    companion object : Entity.Factory<PlayerCooldown>()

    val id: String
    var player: UUID
    var resetAt: Long
}