package gg.traphouse.core.nametag

data class NameTag(val id: String, val info: NametagInfo) {
    val scoreboardPacket = ScoreboardTeamPacketMod(id, info.prefix, info.suffix, info.color, emptyList(), 0)
}
