package me.smp.core

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor

object ComponentHelper {

    /**
     * Creates message like {message} (now/no longer) {ending}
     */
    fun createBoolean(message: String, ending: String, state: Boolean) =
        Component.empty()
            .append(Component.text("$message ", NamedTextColor.YELLOW))
            .append(
                if (state) Component.text("now", NamedTextColor.GREEN) else Component.text(
                    "no longer",
                    NamedTextColor.RED
                )
            )
            .append(Component.text(" $ending", NamedTextColor.YELLOW))
}