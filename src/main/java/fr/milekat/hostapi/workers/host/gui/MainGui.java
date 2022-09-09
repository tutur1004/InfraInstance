package fr.milekat.hostapi.workers.host.gui;

import fr.milekat.hostapi.Main;
import fr.milekat.hostapi.workers.utils.Glowing;
import fr.milekat.hostapi.workers.utils.PlayerHead;
import fr.milekat.hostapi.workers.host.HostAccess;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class MainGui {
    private static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("mainGui")
            .provider(new MainProvider())
            .size(5, 9)
            .title(ChatColor.DARK_AQUA + "Host")
            .closeable(false)
            .build();

    /**
     * Open a new Main host GUI
     */
    public MainGui(Player player) {
        INVENTORY.open(player);
    }

    private static class MainProvider implements InventoryProvider {
        @Override
        public void init(@NotNull Player player, @NotNull InventoryContents contents) {
            //  Fill inventory of glass panes
            ClickableItem empty = ClickableItem.empty(new ItemStack(Material.STAINED_GLASS_PANE,
                    0, new Integer(15).shortValue()));
            contents.set(0,0, empty);
            contents.set(0,1, empty);
            contents.set(0,7, empty);
            contents.set(0,8, empty);
            contents.set(1,0, empty);
            contents.set(1,8, empty);
            contents.set(4,0, empty);
            contents.set(4,8, empty);
            contents.set(5,0, empty);
            contents.set(5,1, empty);
            contents.set(5,7, empty);
            //  Host infos
            contents.set(0, 4, ClickableItem.empty(PlayerHead.getTextureSkull(PlayerHead.Simplistic_Steve,
                    Main.SERVER_NAME, getHostInfoLore())));
            //  Open WhiteList GUI
            contents.set(2, 2, ClickableItem.of(PlayerHead.getPlayerSkull(player.getName(),
                            "Whitelisted players", getWhiteListLore()), e -> new WhiteList(player)));
            //  Toggle game access
            contents.set(2, 3, ClickableItem.of(getRedButton(),
                    getAccessConsumer(contents, 3, getRedButton())));
            contents.set(2, 4, ClickableItem.of(getGoldButton(),
                    getAccessConsumer(contents, 4, getGoldButton())));
            contents.set(2, 5, ClickableItem.of(getGreenButton(),
                    getAccessConsumer(contents, 5, getGreenButton())));
            //  Open WaitList GUI
            contents.set(2, 6, ClickableItem.of(getWaitListButton(), e -> new WaitList(player)));
            //  Close GUI
            contents.set(5, 4, ClickableItem.of(getCloseButton(), e -> INVENTORY.close(player)));
            //  Cancel host (With validation)
            contents.set(5, 8, ClickableItem.of(getCancelButton(), e -> new CancelHost(player)));
        }

        @Override
        public void update(Player player, InventoryContents contents) {}

        /**
         * Get PRIVATE button ItemStack
         */
        private @NotNull ItemStack getRedButton() {
            ItemStack item = PlayerHead.getTextureSkull(PlayerHead.Rose_Red, "§cPRIVATE HOST",
                    Arrays.asList(ChatColor.GOLD + "Click to turn", "this host in", "private mode"));
            if (Main.HOST_ACCESS.getAccess().equals(HostAccess.AccessStates.PRIVATE)) {
                Glowing.addGlow(item);
            }
            return item;
        }

        /**
         * Get REQUEST_ONLY_BUTTON button ItemStack
         */
        private @NotNull ItemStack getGoldButton() {
            ItemStack item = PlayerHead.getTextureSkull(PlayerHead.Gold, "§cWAIT LIST",
                    Arrays.asList(ChatColor.GOLD + "Click to turn", "this host in", "WaitList mode"));
            if (Main.HOST_ACCESS.getAccess().equals(HostAccess.AccessStates.REQUEST_TO_JOIN)) {
                Glowing.addGlow(item);
            }
            return item;
        }

        /**
         * Get OPEN button ItemStack
         */
        private @NotNull ItemStack getGreenButton() {
            ItemStack item = PlayerHead.getTextureSkull(PlayerHead.Lime_Green, "§cOPEN",
                    Arrays.asList(ChatColor.GOLD + "Click to turn", "this host in", "Open mode"));
            if (Main.HOST_ACCESS.getAccess().equals(HostAccess.AccessStates.OPEN)) {
                Glowing.addGlow(item);
            }
            return item;
        }

        @Contract(pure = true)
        private @NotNull Consumer<InventoryClickEvent> getAccessConsumer
                (InventoryContents contents, int slot, ItemStack button) {
            return e-> contents.set(1, slot, ClickableItem.of(button, getAccessConsumer(contents, slot, button)));
        }

        private @NotNull List<String> getHostInfoLore() {
            List<String> hostInfos = new ArrayList<>();
            hostInfos.add("Host: " + Main.HOST_PLAYER.getName());
            hostInfos.add("Game: " + Main.GAME);
            hostInfos.add("Version: " + Main.VERSION);
            return hostInfos;
        }

        private @NotNull List<String> getWhiteListLore() {
            List<String> whiteListLore = new ArrayList<>();
            whiteListLore.add("Edit Whitelist");
            whiteListLore.add("Or kick players");
            return whiteListLore;
        }

        private @NotNull ItemStack getWaitListButton() {
            ItemStack waitListButton = new ItemStack(Material.DARK_OAK_DOOR_ITEM);
            ItemMeta meta = waitListButton.getItemMeta();
            meta.setDisplayName("Waiting players");
            List<String> waitListLore = new ArrayList<>();
            waitListLore.add("Waiting players");
            meta.setLore(waitListLore);
            waitListButton.setItemMeta(meta);
            return waitListButton;
        }

        /**
         * Get close button ItemStack
         */
        private @NotNull ItemStack getCloseButton() {
            ItemStack closeButton = new ItemStack(Material.ARROW);
            ItemMeta meta = closeButton.getItemMeta();
            meta.setDisplayName("§cClose menu");
            closeButton.setItemMeta(meta);
            return closeButton;
        }

        /**
         * Get cancel button ItemStack
         */
        private @NotNull ItemStack getCancelButton() {
            ItemStack cancelButton = new ItemStack(Material.BARRIER);
            ItemMeta meta = cancelButton.getItemMeta();
            meta.setDisplayName("§4Cancel this host");
            List<String> waitListLore = new ArrayList<>();
            waitListLore.add("§cWarning this will");
            waitListLore.add("§cRemove this server");
            waitListLore.add("§cTicket will be refund");
            meta.setLore(waitListLore);
            cancelButton.setItemMeta(meta);
            return cancelButton;
        }
    }
}
