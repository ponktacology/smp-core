package me.smp.core

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

class ItemBuilder(private val itemStack: ItemStack) {

    constructor(material: Material, amount: Int = 1) : this(ItemStack(material, amount))

    fun name(component: Component): ItemBuilder {
        updateMeta { it.displayName(component) }
        return this
    }

    fun amount(amount: Int): ItemBuilder {
        itemStack.amount = amount
        return this
    }

    fun lore(vararg components: Component): ItemBuilder {
        updateMeta { it.lore(mutableListOf(*components)) }
        return this
    }

    fun lore(components: List<Component>): ItemBuilder {
        updateMeta { it.lore(components.toMutableList()) }
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

    private fun updateMeta(update: (ItemMeta) -> (Unit)) {
        val meta = itemStack.itemMeta
        update(meta)
        itemStack.itemMeta = meta
    }

    fun build() = itemStack
}
