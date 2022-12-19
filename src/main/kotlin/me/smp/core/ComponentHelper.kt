package me.smp.core

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor

object ComponentHelper {

    /**
     * Creates message like {message} (now/no longer) {ending}
     */
    fun createBoolean(prefix: String, suffix: String, state: Boolean) =
        Component.empty()
            .append(Component.text("$prefix ", NamedTextColor.YELLOW))
            .append(
                if (state) Component.text("now", NamedTextColor.GREEN) else Component.text(
                    "no longer",
                    NamedTextColor.RED
                )
            )
            .append(Component.text(" $suffix", NamedTextColor.YELLOW))
}
