package gg.traphouse.core

import gg.traphouse.shared.Duration
import java.util.concurrent.TimeUnit

open class Cooldown(
    override val duration: Duration,
    @Volatile override var startedAt: Long = System.currentTimeMillis()
) :
    Expiring {

    constructor(duration: Long, unit: TimeUnit) : this(Duration(unit.toMillis(duration)))

    constructor(millis: Long) : this(Duration(millis))

    constructor(millis: Long, startedAt: Long) : this(Duration(millis), startedAt)
}
