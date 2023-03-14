package fr.milekat.infra.workers.utils;

import fr.milekat.infra.Main;
import fr.milekat.infra.api.classes.AccessStates;
import fr.milekat.infra.api.classes.User;
import fr.milekat.infra.storage.exeptions.StorageExecuteException;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.Pagination;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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

    /**
     * Method to set the player stats button
     */
    public static void setPlayerStats(Player player, InventoryContents contents, int row, int column) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), ()-> {
            try {
                User user = Main.getStorage().getUserCache(player.getUniqueId());
                if (user==null) return;
                List<String> stats = new ArrayList<>();
                stats.add(player.getName());
                stats.add("Tickets: " + user.getTickets());
                ItemStack playerStats = PlayerHead.getPlayerSkull(player.getName(), "Your stats", stats);
                contents.set(row, column, ClickableItem.empty(playerStats));
            } catch (StorageExecuteException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Method to get a custom infra server icon
     */
    public static @NotNull ItemStack getIcon(@NotNull String icon) {
        ItemStack itemStack = new ItemStack(Material.BARRIER);
        if (icon.startsWith("t:")) {
            itemStack = PlayerHead.getTextureSkull(icon.substring(2));
        } else {
            try {
                itemStack.setType(Material.valueOf(icon));
            } catch (IllegalArgumentException exception) {
                itemStack.setType(Material.BARRIER);
            }
        }
        return itemStack;
    }

    /**
     * Method to get a custom infra server icon
     */
    public static @NotNull ItemStack getIcon(String icon, String name) {
        ItemStack itemStack = getIcon(icon);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    /**
     * Method to get a custom infra server icon
     */
    public static @NotNull ItemStack getIcon(String icon, String name, @NotNull List<String> lore) {
        ItemStack itemStack = getIcon(icon, name);
        ItemMeta meta = itemStack.getItemMeta();
        List<String> formattedLore = new ArrayList<>();
        lore.stream()
                .filter(Objects::nonNull)
                .forEach(str -> formattedLore.add(ChatColor.translateAlternateColorCodes('&', str)));
        meta.setLore(formattedLore);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    /**
     * Method to get a custom infra server icon
     */
    public static @NotNull ItemStack getIcon(@NotNull AccessStates icon, String name, @NotNull List<String> lore) {
        ItemStack itemStack = new ItemStack(Material.STAINED_CLAY);
        itemStack.setDurability((short) (icon.equals(AccessStates.OPEN) ? 5 : 4));
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        List<String> formattedLore = new ArrayList<>();
        lore.stream()
                .filter(Objects::nonNull)
                .forEach(str -> formattedLore.add(ChatColor.translateAlternateColorCodes('&', str)));
        meta.setLore(formattedLore);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static void pagesDefault(@NotNull Player player, @NotNull InventoryContents contents, GuiPages inventory) {
        contents.fillBorders(Gui.empty());
        setPlayerStats(player, contents, 0, 4);
        Pagination pagination = contents.pagination();
        pagination.setItemsPerPage(28);
        //  Close GUI
        contents.set(4, 4, ClickableItem.of(Gui.getCloseButton(), e ->
                inventory.getInventory().getParent().ifPresent(smartInventory -> smartInventory.open(player))));
    }

    public static void pagesButtons(@NotNull Player player, @NotNull InventoryContents contents, GuiPages inventory) {
        Pagination pagination = contents.pagination();
        pagination.setItemsPerPage(21);
        if (!pagination.isFirst()) {
            contents.set(4, 0, ClickableItem.of(
                    PlayerHead.getTextureSkull(PlayerHead.Arrow_Left, "Previous"),
                    e -> {
                        inventory.getInventory().open(player, pagination.previous().getPage());
                        inventory.updatePages(contents, player);
                    }));
        }
        if (!pagination.isLast()) {
            contents.set(4, 8, ClickableItem.of(
                    PlayerHead.getTextureSkull(PlayerHead.Arrow_Right, "Next"),
                    e -> {
                        inventory.getInventory().open(player, pagination.next().getPage());
                        inventory.updatePages(contents, player);
                    }));
        }
    }

    /**
     * Little algorithm to fill in square pattern an inventory between 2 slots
     * @param contents Inventory
     */
    public static void fillPage(@NotNull InventoryContents contents,
                                int fromRow, int fromColumn, int toRow, int toColumn) {
        List<ClickableItem> items = Arrays.asList(contents.pagination().getPageItems());
        for(int row = fromRow; row <= toRow; row++) {
            for(int column = fromColumn; column <= toColumn; column++) {
                int index = (column - fromColumn) + ((row - fromRow) * ((toColumn + 1) - fromColumn));
                if (items.size() <= index || items.get(index)==null || items.get(index).getItem()==null) {
                    return;
                }
                contents.set(row, column, items.get(index));
            }
        }
    }

    public interface GuiPages {
        SmartInventory getInventory();

        void updatePages(InventoryContents contents, Player player);
    }
}
