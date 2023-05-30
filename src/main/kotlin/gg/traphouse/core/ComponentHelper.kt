package gg.traphouse.core

import gg.traphouse.core.util.SenderUtil.sendError
import gg.traphouse.core.util.SenderUtil.sendSuccess
import org.bukkit.entity.Player

object ComponentHelper {


    /**
     * Creates message like {message} (now/no longer) {ending}
     */
    fun Player.sendStateComponent(text: String, state: Boolean) {
        if (state) this.sendSuccess(text.format("włączony"))
        else this.sendError(text.format("wyłączony"))
    }
}
