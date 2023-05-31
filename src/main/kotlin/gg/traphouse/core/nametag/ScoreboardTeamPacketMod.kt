package gg.traphouse.core.nametag

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.utility.MinecraftReflection
import com.comphenix.protocol.wrappers.WrappedChatComponent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.*

class ScoreboardTeamPacketMod {
    private val packet: PacketContainer

    constructor(
        name: String,
        prefix: Component,
        suffix: Component,
        color: ChatColor,
        players: Collection<String>,
        paramInt: Int
    ) {
        packet = ProtocolLibrary.getProtocolManager()
            .createPacket(PacketType.Play.Server.SCOREBOARD_TEAM) // Create a new scoreboard team packet

        try {
            packet.integers.write(0, paramInt) // Mode -create team
            packet.strings.write(0, name) // Give the team a name
            if (paramInt == 0 || paramInt == 2) {
                val optStruct = packet.optionalStructures.read(0) // Team Data
                if (optStruct.isPresent) { // Make sure the structure exists (it always does)
                    val struct = optStruct.get()
                    struct.chatComponents.write(0, WrappedChatComponent.fromText(name)) // TeamName
                    struct.chatComponents.write(
                        1,
                        WrappedChatComponent.fromJson(
                            GsonComponentSerializer.gson().serialize(prefix)
                        )
                    ) // Team Prefix
                    struct.chatComponents.write(
                        2,
                        WrappedChatComponent.fromJson(
                            GsonComponentSerializer.gson().serialize(suffix)
                        )
                    ) // Team Suffix
                    struct.integers.write(
                        0,
                        0x01
                    ) // Bit mask. 0x01: Allow friendly fire, 0x02: can see invisible players on same team.
                    struct.getEnumModifier(
                        ChatColor::class.java,
                        MinecraftReflection.getMinecraftClass("EnumChatFormat")
                    ).write(0, color)
                    packet.optionalStructures.write(
                        0,
                        Optional.of(struct)
                    ) // Set the changed structure as the one to use in the packet
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (paramInt == 0) {
            val collection = packet.modifier.read(2) as? MutableCollection<String> ?: ArrayList<String>()
            collection.addAll(players)
            packet.modifier.write(2, collection)
        }
    }

    constructor(name: String, player: String, paramInt: Int) {
        packet = ProtocolLibrary.getProtocolManager()
            .createPacket(PacketType.Play.Server.SCOREBOARD_TEAM) // Create a new scoreboard team packet
        try {
            packet.integers.write(0, paramInt) // Mode -create team
            packet.strings.write(0, name) // Give the team a name
            if (paramInt == 0 || paramInt == 2) {
                val optStruct = packet.optionalStructures.read(0) // Team Data
                if (optStruct.isPresent) { // Make sure the structure exists (it always does)
                    val struct = optStruct.get()
                    struct.integers.write(0, 1)
                    packet.optionalStructures.write(
                        0,
                        Optional.of(struct)
                    ) // Set the changed structure as the one to use in the packet
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val collection = packet.modifier.read(2) as? MutableCollection<String> ?: ArrayList<String>()
        collection.add(player)
        packet.modifier.write(2, collection)
    }

    fun sendToPlayer(bukkitPlayer: Player?) {
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(bukkitPlayer, packet)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
