package gg.traphouse.core.util

import java.util.regex.Pattern

object StringUtil {


    private val CUSTOM_CHARS = mapOf(
        'a' to "ᴀ",
        'b' to "ʙ",
        'c' to "ᴄ",
        'd' to "ᴅ",
        'e' to "ᴇ",
        'e' to "ᴇ",
        'f' to "ғ",
        'g' to "ɢ",
        'h' to "ʜ",
        'i' to "ɪ",
        'j' to "ᴊ",
        'k' to "ᴋ",
        'l' to "ʟ",
        'm' to "ᴍ",
        'n' to "ɴ",
        'o' to "ᴏ",
        'p' to "ᴘ",
        'q' to "ᴏ̨",
        'r' to "ʀ",
        's' to "s",
        't' to "ᴛ",
        'u' to "ᴜ",
        'w' to "ᴡ",
        'x' to "x",
        'y' to "ʏ",
        'z' to "ᴢ")

    private val whiteSpace = Pattern.compile("\\s+")

    fun splitByWhiteSpace(text: String) = text.split(whiteSpace)

    fun customFont(text: String): String {
        return text.lowercase().toCharArray()
            .map {
                return@map CUSTOM_CHARS.getOrDefault(it, it.toString())
            }
            .joinToString("")
    }
}
