package fr.milekat.infra.workers.host.gui;

import fr.milekat.infra.Main;
import fr.milekat.infra.api.classes.AccessStates;
import fr.milekat.infra.workers.utils.Glowing;
import fr.milekat.infra.workers.utils.PlayerHead;
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
import java.util.List;
import java.util.function.Consumer;

public class MainGui {
    private static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("mainGui")
            .manager(Main.INVENTORY_MANAGER)
            .provider(new MainProvider())
            .size(5, 9)
            .title(ChatColor.DARK_AQUA + "Host: " + Main.SERVER_NAME)
            .closeable(true)
            .build();

    public static @NotNull ClickableItem empty() {
        ClickableItem empty = ClickableItem.empty(new ItemStack(Material.STAINED_GLASS_PANE,
                1, new Integer(15).shortValue()));
        ItemMeta meta = empty.getItem().getItemMeta();
        meta.setDisplayName(" ");
        empty.getItem().setItemMeta(meta);
        return empty;
    }

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
            ClickableItem empty = empty();
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
            update(player, contents);
            //  Open WhiteList GUI
            contents.set(2, 1, ClickableItem.of(PlayerHead.getTextureSkull(PlayerHead.Simplistic_Steve,
                    "Whitelisted players", getWhiteListLore()), e -> new WhiteList(player)));
            //  Open WaitList GUI
            contents.set(2, 7, ClickableItem.of(getWaitListButton(), e -> new WaitList(player)));
            //  Close GUI
            contents.set(4, 4, ClickableItem.of(getCloseButton(), e -> INVENTORY.close(player)));
            //  Cancel host (With validation)
            contents.set(4, 8, ClickableItem.of(getCancelButton(), e -> new CancelHost(player)));
        }

        @Override
        public void update(@NotNull Player player, @NotNull InventoryContents contents) {
            //  Host infos
            contents.set(0, 4, ClickableItem.empty(PlayerHead.getPlayerSkull(player.getName(),
                    Main.SERVER_NAME, getHostInfoLore())));
            //  Toggle game access
            contents.set(2, 3, ClickableItem.of(getRedButton(),
                    getAccessConsumer(player, contents, AccessStates.PRIVATE)));
            contents.set(2, 4, ClickableItem.of(getGoldButton(),
                    getAccessConsumer(player, contents, AccessStates.REQUEST_TO_JOIN)));
            contents.set(2, 5, ClickableItem.of(getGreenButton(),
                    getAccessConsumer(player, contents, AccessStates.OPEN)));
        }

        /**
         * Get PRIVATE button ItemStack
         */
        private @NotNull ItemStack getRedButton() {
            ItemStack item = new ItemStack(Material.STAINED_CLAY, 1 , (short) 14);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§cPRIVATE HOST");
            List<String> waitListLore = new ArrayList<>();
            waitListLore.add(ChatColor.GOLD + "Click to turn");
            waitListLore.add(ChatColor.GOLD + "this host in");
            waitListLore.add(ChatColor.GOLD + "private mode");
            meta.setLore(waitListLore);
            item.setItemMeta(meta);
            /*
            if (Main.HOST_ACCESS.getAccess().equals(InstanceAccess.AccessStates.PRIVATE)) {
                Glowing.addGlow(item);
            }
            */
            return item;
        }

        /**
         * Get REQUEST_ONLY_BUTTON button ItemStack
         */
        private @NotNull ItemStack getGoldButton() {
            ItemStack item = new ItemStack(Material.STAINED_CLAY, 1 , (short) 4);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§cWAIT LIST");
            List<String> waitListLore = new ArrayList<>();
            waitListLore.add(ChatColor.GOLD + "Click to turn");
            waitListLore.add(ChatColor.GOLD + "this host in");
            waitListLore.add(ChatColor.GOLD + "waitList mode");
            meta.setLore(waitListLore);
            item.setItemMeta(meta);
            /*
            if (Main.HOST_ACCESS.getAccess().equals(InstanceAccess.AccessStates.REQUEST_TO_JOIN)) {
                Glowing.addGlow(item);
            }
            */
            return item;
        }

        /**
         * Get OPEN button ItemStack
         */
        private @NotNull ItemStack getGreenButton() {
            ItemStack item = new ItemStack(Material.STAINED_CLAY, 1 , (short) 5);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§cOPEN");
            List<String> waitListLore = new ArrayList<>();
            waitListLore.add(ChatColor.GOLD + "Click to turn");
            waitListLore.add(ChatColor.GOLD + "this host in");
            waitListLore.add(ChatColor.GOLD + "open mode");
            meta.setLore(waitListLore);
            item.setItemMeta(meta);
            /*
            if (Main.HOST_ACCESS.getAccess().equals(AccessStates.OPEN)) {
                Glowing.addGlow(item);
            }
            */
            return item;
        }

        @Contract(pure = true)
        private @NotNull Consumer<InventoryClickEvent> getAccessConsumer
                (Player player, InventoryContents contents, AccessStates access) {
            return e-> {
                //  Main.HOST_ACCESS.setAccess(access);
                update(player, contents);
            };
        }

        private @NotNull List<String> getHostInfoLore() {
            List<String> hostInfos = new ArrayList<>();
            hostInfos.add("Host: " + Main.HOST_PLAYER.getName());
            hostInfos.add("Game: " + Main.GAME);
            hostInfos.add("Version: " + Main.VERSION);
            hostInfos.add("Access: " /*+ Main.HOST_ACCESS.getAccess().name()*/);
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
