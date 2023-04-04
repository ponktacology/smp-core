package gg.traphouse.core.chat

import java.util.regex.Pattern

private val LINK_FILTER =
    Pattern.compile("^(http://www\\.|https://www\\.|http://|https://)?[a-z0-9]+([\\-.][a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(/.*)?$")
private val ADDRESS_FILTER =
    Pattern.compile("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])([.,])){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$")

class ChatFilter {

    fun isFiltered(message: String): Boolean {
        val replacedMessage = message

        println(message)

        return LINK_FILTER.matcher(replacedMessage).matches() || ADDRESS_FILTER.matcher(replacedMessage).matches()
    }
}
