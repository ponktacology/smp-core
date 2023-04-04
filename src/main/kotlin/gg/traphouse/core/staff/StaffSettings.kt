package gg.traphouse.core.staff

import java.util.UUID

data class StaffSettings(val id: Int, val player: UUID, var vanish: Boolean, var god: Boolean, var fly: Boolean)