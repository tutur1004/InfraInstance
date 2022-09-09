package fr.milekat.hostapi.workers.utils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Glowing {
    public static ItemStack addGlow(ItemStack itemStack) {
        itemStack.addEnchantment((itemStack.getType() == Material.BOW) ?
                Enchantment.PROTECTION_ENVIRONMENTAL : Enchantment.ARROW_INFINITE, 1);
        // hides the enchantments
        final ItemMeta meta = itemStack.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemStack.setItemMeta(meta);
        // returns the new itemStack
        return itemStack;
    }
}
