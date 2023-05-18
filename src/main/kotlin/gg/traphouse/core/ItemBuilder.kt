package gg.traphouse.core

import com.destroystokyo.paper.profile.ProfileProperty
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.SkullMeta
import java.util.*

val RESET_COMPONENT = LegacyComponentSerializer.legacyAmpersand().deserialize("&r")

class ItemBuilder(private val itemStack: ItemStack) {

    constructor(material: Material, amount: Int = 1) : this(ItemStack(material, amount))

    fun name(component: Component): ItemBuilder {
        updateMeta { it.displayName(component.decoration(TextDecoration.ITALIC, false)) }
        return this
    }

    fun amount(amount: Int): ItemBuilder {
        itemStack.amount = amount
        return this
    }

    @Suppress("deprecation")
    fun durability(durability: Short): ItemBuilder {
        itemStack.durability = durability
        return this
    }

    fun lore(vararg components: Component): ItemBuilder {
        return lore(mutableListOf(*components))
    }

    fun lore(components: List<Component>): ItemBuilder {
        val components = components.map { it.decoration(TextDecoration.ITALIC, false) }.toMutableList()
        updateMeta { it.lore(components as List<Component>) }
        return this
    }

    fun addLore(component: Component): ItemBuilder {
        updateMeta {
            val lore = it.lore() ?: mutableListOf()
            lore.add(component)
            it.lore(lore)
        }
        return this
    }

    fun addLore(vararg components: Component): ItemBuilder {
        updateMeta {
            val lore = it.lore() ?: mutableListOf()
            lore.addAll(components)
            it.lore(lore)
        }
        return this
    }


    fun enchant(enchantment: Enchantment, level: Int): ItemBuilder {
        updateMeta { it.addEnchant(enchantment, level, true) }
        return this
    }

    fun removeEnchants(): ItemBuilder {
        updateMeta { it.enchants.forEach { (t, _) -> it.removeEnchant(t) } }
        return this
    }

    fun flag(vararg flag: ItemFlag): ItemBuilder {
        updateMeta { it.addItemFlags(*flag) }
        return this
    }

    fun hideAll(): ItemBuilder {
        flag(*ItemFlag.values())
        return this
    }

    @Suppress("deprecation")
    fun skull(owner: String): ItemBuilder {
        require(itemStack.type == Material.PLAYER_HEAD)
        updateMeta {
            (it as SkullMeta).playerProfile = Bukkit.createProfile(owner)
        }
        return this
    }

    @Suppress("deprecation")
    fun skullFromURL(url: String): ItemBuilder {
        require(itemStack.type == Material.PLAYER_HEAD)
        updateMeta {
            val profile = Bukkit.createProfile(UUID.randomUUID())
            profile.clearProperties()
            profile.properties.add(ProfileProperty("textures", url))
            (it as SkullMeta).playerProfile = profile
        }
        return this
    }

    private fun updateMeta(update: (ItemMeta) -> (Unit)) {
        val meta = itemStack.itemMeta
        update(meta)
        itemStack.itemMeta = meta
    }

    fun build() = itemStack
}
