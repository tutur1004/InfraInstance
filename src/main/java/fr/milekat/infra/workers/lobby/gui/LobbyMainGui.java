package fr.milekat.infra.workers.lobby.gui;

import fr.milekat.infra.Main;
import fr.milekat.infra.workers.utils.Gui;
import fr.milekat.infra.workers.utils.PlayerHead;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LobbyMainGui  {
    public final SmartInventory INVENTORY;

    /**
     * Open a new Main lobby GUI
     */
    public LobbyMainGui(@NotNull Player player) {
        INVENTORY = SmartInventory.builder()
                .id("mainGui")
                .manager(Main.INVENTORY_MANAGER)
                .provider(new Provider())
                .size(5, 9)
                .title(ChatColor.DARK_AQUA + "Host panel")
                .closeable(true)
                .build();
        INVENTORY.open(player);
    }

    /**
     * Open a new Main lobby GUI
     */
    public LobbyMainGui(@NotNull Player player, @NotNull SmartInventory parent) {
        INVENTORY = SmartInventory.builder()
                .id("mainGui")
                .manager(Main.INVENTORY_MANAGER)
                .provider(new Provider())
                .size(5, 9)
                .title(ChatColor.DARK_AQUA + "Host panel")
                .closeable(true)
                .parent(parent)
                .build();
        INVENTORY.open(player);
    }

    private class Provider implements InventoryProvider {
        @Override
        public void init(@NotNull Player player, @NotNull InventoryContents contents) {
            //  Fill inventory of glass panes
            ClickableItem empty = Gui.empty();
            contents.set(0,0, empty);
            contents.set(0,1, empty);
            contents.set(0,7, empty);
            contents.set(0,8, empty);
            contents.set(1,0, empty);
            contents.set(1,8, empty);
            contents.set(3,0, empty);
            contents.set(3,8, empty);
            contents.set(4,0, empty);
            contents.set(4,1, empty);
            contents.set(4,7, empty);
            contents.set(4,8, empty);
            //  Player infos
            contents.set(0, 4, ClickableItem.empty(
                    PlayerHead.getPlayerSkull(player.getName(), "Your stats", Gui.getPlayerStats(player))));
            //  Open MyHosts GUI
            contents.set(2, 2, ClickableItem.of(PlayerHead.getPlayerSkull(player.getName(),
                    "My active hosts", Arrays.asList("Click to view", "All your games")),
                    e -> new LobbyMyHosts(player, INVENTORY)));
            //  Open CreateHost GUI
            contents.set(2, 4, ClickableItem.of(getCreateHost(), e ->
                    new LobbyCreateHost(player, INVENTORY)));
            //  Open HostList GUI
            contents.set(2, 6, ClickableItem.of(PlayerHead.getTextureSkull(PlayerHead.Earth,
                    "All accessible games", getHostListLore()), e ->
                    new LobbyListHosts(player, INVENTORY)));
            //  Close GUI
            contents.set(4, 4, ClickableItem.of(Gui.getCloseButton(), e -> INVENTORY.close(player)));
        }

        @Override
        public void update(@NotNull Player player, @NotNull InventoryContents contents) {}

        private @NotNull ItemStack getCreateHost() {
            ItemStack itemStack = new ItemStack(Material.GRASS);
            ItemMeta meta = itemStack.getItemMeta();
            meta.setDisplayName("Create a new Host");
            List<String> lore = new ArrayList<>();
            lore.add("Click to create");
            lore.add("a new host");
            meta.setLore(lore);
            itemStack.setItemMeta(meta);
            return itemStack;
        }

        private @NotNull List<String> getHostListLore() {
            List<String> whiteListLore = new ArrayList<>();
            whiteListLore.add("Click to open");
            whiteListLore.add("All games list");
            return whiteListLore;
        }
    }
}
