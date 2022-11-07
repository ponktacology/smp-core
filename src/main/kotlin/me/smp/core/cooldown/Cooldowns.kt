package me.smp.core.cooldown

import java.util.concurrent.TimeUnit

enum class Cooldowns(override val duration: Long) : CooldownType {

    ASSISTANCE_REQUEST(TimeUnit.MILLISECONDS.convert(45, TimeUnit.SECONDS)),
    ASSISTANCE_REPORT(TimeUnit.MILLISECONDS.convert(30, TimeUnit.SECONDS));

    override val id = this.name
}