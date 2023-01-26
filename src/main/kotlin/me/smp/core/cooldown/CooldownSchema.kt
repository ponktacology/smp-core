package me.smp.core.cooldown

import org.ktorm.entity.Entity
import org.ktorm.schema.*
import java.util.*

fun RemoteCooldown.toDomain(type: CooldownType) = PersistentCooldown(this.id, type, this.resetAt)

fun PersistentCooldown.toRemote(player: UUID) = RemoteCooldown {
    this.id = this@toRemote.id
    this.player = player
    this.type = this@toRemote.type.type
    this.resetAt = this@toRemote.startedAt
}

object CooldownsTable : Table<RemoteCooldown>("cooldowns") {
    val id = int("id").primaryKey().bindTo { it.id }
    val player = uuid("player").bindTo { it.player }
    val type = varchar("type").primaryKey().bindTo { it.type }
    val resetAt = long("reset_at").bindTo { it.resetAt }
}

interface RemoteCooldown : Entity<RemoteCooldown> {
    companion object : Entity.Factory<RemoteCooldown>()

    var id: Int
    var player: UUID
    var type: String
    var resetAt: Long
}
