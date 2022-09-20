package me.smp.core

import java.util.*

interface UUIDCache {

    fun loadCache(uuid: UUID)
    fun flushCache(uuid: UUID)

}