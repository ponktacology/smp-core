package me.smp.core

import java.util.*

interface VerifiableUUIDCache : UUIDCache {
    fun verify(uuid: UUID): Boolean
}