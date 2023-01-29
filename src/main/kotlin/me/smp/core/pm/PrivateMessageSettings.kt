package me.smp.core.pm

import java.util.UUID

data class PrivateMessageSettings(val id: Int, val uuid: UUID, var enabled: Boolean)