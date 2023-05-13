package gg.traphouse.core.nametag

data class NameTag(val name: String, val info: NameTagInfo) {

    val teamAddPacket: ScoreboardTeamPacketMod =
        ScoreboardTeamPacketMod(name, info.prefix, info.suffix, info.color, emptyList(), 0)

}
