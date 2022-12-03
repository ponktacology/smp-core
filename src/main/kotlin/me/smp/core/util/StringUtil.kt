package me.smp.core.util

import java.util.regex.Pattern

object StringUtil {

    private val whiteSpace = Pattern.compile("\\s+")

    fun splitByWhiteSpace(text: String) = text.split(whiteSpace)
}
