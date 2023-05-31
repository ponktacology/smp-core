package gg.traphouse.core

enum class SocialLinks(val subdomain: String, val displayName: String) {


    DISCORD("dc", "Discord"),
    WEBSITE("www", "Strona"),
    STORE("sklep", "Sklep");

    companion object {
        const val BASE_URL = "traphouse.gg"
    }

}