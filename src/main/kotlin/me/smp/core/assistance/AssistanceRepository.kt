package me.smp.core.assistance

import me.smp.core.Config
import java.util.*
import java.util.concurrent.ConcurrentHashMap


// Make this persistent
class AssistanceRepository {

    private val reportCache = ConcurrentHashMap<UUID, Long>()
    private val requestCache = ConcurrentHashMap<UUID, Long>()

    fun isOnReportCooldown(uuid: UUID) = System.currentTimeMillis() - (reportCache[uuid] ?: 0) < Config.REPORT_COOLDOWN_TIME

    fun isOnRequestCooldown(uuid: UUID) = System.currentTimeMillis() - (requestCache[uuid] ?: 0) < Config.REPORT_COOLDOWN_TIME

    fun resetReportCooldown(uuid: UUID) {
        reportCache[uuid] = System.currentTimeMillis()
    }

    fun resetRequestCooldown(uuid: UUID) {
        requestCache[uuid] = System.currentTimeMillis()
    }

    fun flushCache(uuid: UUID) {
        reportCache.remove(uuid)
        requestCache.remove(uuid)
    }


}