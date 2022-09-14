package me.smp.core

import java.util.concurrent.TimeUnit

class Cooldown(override val duration: Duration, override var startedAt: Long = System.currentTimeMillis()) : Expiring {

    constructor(duration: Long, unit: TimeUnit) : this(Duration(unit.toMillis(duration)))
}