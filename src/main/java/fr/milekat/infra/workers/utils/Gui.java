package fr.milekat.infra.workers.utils;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Gui {
    /**
     * Get empty glass pane
     */
    public static @NotNull ClickableItem empty() {
        ClickableItem empty = ClickableItem.empty(new ItemStack(Material.STAINED_GLASS_PANE,
                1, new Integer(15).shortValue()));
        ItemMeta meta = empty.getItem().getItemMeta();
        meta.setDisplayName(" ");
        empty.getItem().setItemMeta(meta);
        return empty;
    }

    /**
     * Get close button ItemStack
     */
    public static @NotNull ItemStack getCloseButton() {
        ItemStack closeButton = new ItemStack(Material.ARROW);
        ItemMeta meta = closeButton.getItemMeta();
        meta.setDisplayName("§cClose menu");
        closeButton.setItemMeta(meta);
        return closeButton;
    }

    public static List<String> getPlayerStats(Player player) {
        List<String> stats = new ArrayList<>();
        stats.add(player.getName());
        stats.add(player.getUniqueId().toString());
        return stats;
    }

    /**
     * Method to get a custom infra server icon
     */
    public static ItemStack getIcon(String icon) {
        ItemStack itemStack = new ItemStack(Material.BARRIER);
        if (icon.startsWith("t:")) {
            itemStack = PlayerHead.getTextureSkull(icon);
        } else {
            itemStack.setType(Material.valueOf(icon));
        }
        return itemStack;
    }

    /**
     * Method to get a custom infra server icon
     */
    public static ItemStack getIcon(String icon, String name) {
        ItemStack itemStack = getIcon(icon);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(name);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    /**
     * Method to get a custom infra server icon
     */
    public static ItemStack getIcon(String icon, String name, List<String> lore) {
        ItemStack itemStack = getIcon(icon);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static void fillPage(@NotNull InventoryContents contents,
                                int fromRow, int fromColumn, int toRow, int toColumn,
                                List<ClickableItem> item) {
        for(int row = fromRow; row <= toRow; row++) {
            for(int column = fromColumn; column <= toColumn; column++) {
                int index = (column - fromColumn) + ((row - fromRow) * ((toColumn + 1) - fromColumn));
                if (item.size() <= index) {
                    return;
                }
                contents.set(row, column, item.get(index));
            }
        }
    }
}
