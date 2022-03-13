@file:Suppress("UsePropertyAccessSyntax")

package com.willfp.stattrackers.display

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.display.Display
import com.willfp.eco.core.display.DisplayModule
import com.willfp.eco.core.display.DisplayPriority
import com.willfp.eco.core.fast.FastItemStack
import com.willfp.eco.util.NumberUtils
import com.willfp.eco.util.StringUtils
import com.willfp.stattrackers.stats.statTracker
import com.willfp.stattrackers.stats.trackedStats
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

class StatTrackersDisplay(plugin: EcoPlugin) : DisplayModule(plugin, DisplayPriority.HIGHEST) {
    override fun display(
        itemStack: ItemStack,
        vararg args: Any
    ) {
        val meta = itemStack.itemMeta ?: return

        if (!displayItemMeta(meta)) {
            displayTrackerMeta(meta)
        }

        itemStack.itemMeta = meta
    }

    private fun displayTrackerMeta(meta: ItemMeta) {
        val stat = meta.statTracker ?: return

        val trackerMeta = stat.tracker.itemMeta ?: return

        meta.setDisplayName(trackerMeta.displayName)

        val lore = mutableListOf<String>()

        lore.addAll(FastItemStack.wrap(stat.tracker).lore.map { Display.PREFIX + it })

        meta.addEnchant(Enchantment.DAMAGE_UNDEAD, 1, true)
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)

        val itemLore = getLore(meta)

        lore.addAll(itemLore)

        meta.lore = lore
    }

    private fun displayItemMeta(meta: ItemMeta): Boolean {
        val stats = meta.trackedStats
        if (stats.isEmpty()) {
            return false
        }

        val itemLore = getLore(meta)

        for (stat in stats) {
            itemLore.add(
                Display.PREFIX + stat.stat.display
                    .replace("%value%", NumberUtils.format(stat.value))
            )
        }

        meta.lore = itemLore

        return true
    }

    private fun getLore(meta: ItemMeta): MutableList<String> {
        var itemLore: MutableList<String>? = ArrayList()

        if (meta.hasLore()) {
            itemLore = meta.lore
        }

        if (itemLore == null) {
            itemLore = ArrayList()
        }

        return itemLore
    }
}
