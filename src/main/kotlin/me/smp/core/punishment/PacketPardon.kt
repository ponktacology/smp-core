package me.smp.core.punishment

import me.smp.shared.punishment.Punishment
import java.util.*

class PacketPardon(
    val player: UUID,
    val type: Punishment.Type,
    val issuer: UUID,
    val reason: String,
    val silent: Boolean
)
