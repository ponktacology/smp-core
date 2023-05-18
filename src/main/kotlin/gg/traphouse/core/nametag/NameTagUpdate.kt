package gg.traphouse.core.nametag

import java.util.*

data class NameTagUpdate(val toRefresh: UUID, val refreshFor: UUID)
