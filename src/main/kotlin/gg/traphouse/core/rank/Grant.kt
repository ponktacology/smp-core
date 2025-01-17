package gg.traphouse.core.rank

import gg.traphouse.shared.Manageable
import gg.traphouse.shared.duration
import org.ktorm.entity.Entity
import org.ktorm.schema.*

object Grants : Table<Grant>("grants") {
    val id = int("id").primaryKey().bindTo { it.id }
    val player = uuid("player").bindTo { it.player }
    val rank = enum<Rank>("rank").bindTo { it.rank }
    val addedAt = long("added_at").bindTo { it.addedAt }
    val issuer = uuid("issuer").bindTo { it.issuer }
    val reason = varchar("reason").bindTo { it.reason }
    val duration = duration("duration").bindTo { it.duration }
    val removed = boolean("removed").bindTo { it.removed }
    val removedAt = long("removed_at").bindTo { it.removedAt }
    val remover = uuid("remover").bindTo { it.remover }
    val removeReason = varchar("remove_reason").bindTo { it.removeReason }
}

interface Grant : Entity<Grant>, Manageable {
    companion object : Entity.Factory<Grant>()

    var rank: Rank
}
