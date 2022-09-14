package me.smp.core

import java.util.UUID

interface UUIDCache {
    fun flushCache(uuid: UUID)
    fun loadCache(uuid: UUID)
}