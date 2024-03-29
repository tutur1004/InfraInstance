package fr.milekat.infra.workers.host.gui;

import fr.milekat.infra.Main;
import fr.milekat.infra.api.classes.AccessStates;
import fr.milekat.infra.storage.exeptions.StorageExecuteException;
import fr.milekat.infra.workers.utils.Glowing;
import fr.milekat.infra.workers.utils.Gui;
import fr.milekat.infra.workers.utils.PlayerHead;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
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

public class HostMainGui {
    public final SmartInventory INVENTORY;

    /**
     * Open a new Main host GUI
     */
    public HostMainGui(@NotNull Player player) {
        INVENTORY = SmartInventory.builder()
                .id("mainGui")
                .manager(Main.INVENTORY_MANAGER)
                .provider(new Provider())
                .size(5, 9)
                .title(Main.getConfigs().getMessage("messages.host.gui.main.tittle")
                        .replaceAll("<SRV_NAME>", Main.SERVER_NAME))
                .closeable(true)
                .build();
        INVENTORY.open(player);
    }

    /**
     * Open a new Main host GUI
     */
    public HostMainGui(@NotNull Player player, @NotNull SmartInventory parent) {
        INVENTORY = SmartInventory.builder()
                .id("mainGui")
                .manager(Main.INVENTORY_MANAGER)
                .provider(new Provider())
                .size(5, 9)
                .title(Main.getConfigs().getMessage("messages.host.gui.main.tittle")
                        .replaceAll("<SRV_NAME>", Main.SERVER_NAME))
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
            update(player, contents);
            //  Open WhiteList GUI
            contents.set(2, 1, ClickableItem.of(getWhiteListButton(),
                    e -> new HostWhiteList(player, INVENTORY)));
            //  Open WaitList GUI
            contents.set(2, 7, ClickableItem.of(getWaitListButton(),
                    e -> new HostWaitList(player, INVENTORY)));
            //  Close GUI
            contents.set(4, 4, ClickableItem.of(Gui.getCloseButton(),
                    e -> INVENTORY.close(player)));
            //  Cancel host (With validation)
            contents.set(4, 8, ClickableItem.of(getCancelButton(),
                    e -> new HostCancelConfirm(player)));
        }

        private @NotNull ItemStack getWhiteListButton() {
            return PlayerHead.getTextureSkull(PlayerHead.Simplistic_Steve,
                    Main.getConfigs().getMessage("messages.host.gui.main.buttons.whitelist.tittle"),
                    Main.getConfigs().getMessages("messages.host.gui.main.buttons.whitelist.lore"));
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
            meta.setDisplayName(Main.getConfigs().getMessage("messages.host.gui.main.buttons.private.tittle"));
            meta.setLore(Main.getConfigs().getMessages("messages.host.gui.main.buttons.private.lore"));
            item.setItemMeta(meta);
            if (Main.HOST_INSTANCE.getAccess().equals(AccessStates.PRIVATE)) {
                Glowing.addGlow(item);
            }
            return item;
        }

        /**
         * Get REQUEST_ONLY_BUTTON button ItemStack
         */
        private @NotNull ItemStack getGoldButton() {
            ItemStack item = new ItemStack(Material.STAINED_CLAY, 1 , (short) 4);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(Main.getConfigs().getMessage("messages.host.gui.main.buttons.wait.tittle"));
            meta.setLore(Main.getConfigs().getMessages("messages.host.gui.main.buttons.wait.lore"));
            item.setItemMeta(meta);
            if (Main.HOST_INSTANCE.getAccess().equals(AccessStates.REQUEST_TO_JOIN)) {
                Glowing.addGlow(item);
            }
            return item;
        }

        /**
         * Get OPEN button ItemStack
         */
        private @NotNull ItemStack getGreenButton() {
            ItemStack item = new ItemStack(Material.STAINED_CLAY, 1 , (short) 5);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(Main.getConfigs().getMessage("messages.host.gui.main.buttons.open.tittle"));
            meta.setLore(Main.getConfigs().getMessages("messages.host.gui.main.buttons.open.lore"));
            item.setItemMeta(meta);
            if (Main.HOST_INSTANCE.getAccess().equals(AccessStates.OPEN)) {
                Glowing.addGlow(item);
            }
            return item;
        }

        @Contract(pure = true)
        private @NotNull Consumer<InventoryClickEvent> getAccessConsumer
                (Player player, InventoryContents contents, AccessStates access) {
            return event -> {
                try {
                    Main.HOST_INSTANCE.setAccess(access);
                    Main.getStorage().updateInstanceState(Main.HOST_INSTANCE);
                    update(player, contents);
                } catch (StorageExecuteException exception) {
                    if (Main.DEBUG) {
                        Main.getOwnLogger().info("Error while trying to update instance state in Storage.");
                        exception.printStackTrace();
                    }
                }
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

        private @NotNull ItemStack getWaitListButton() {
            ItemStack waitListButton = new ItemStack(Material.DARK_OAK_DOOR_ITEM);
            ItemMeta meta = waitListButton.getItemMeta();
            meta.setDisplayName(Main.getConfigs().getMessage("messages.host.gui.main.buttons.wait-list.tittle"));
            meta.setLore(Main.getConfigs().getMessages("messages.host.gui.main.buttons.wait-list.lore"));
            waitListButton.setItemMeta(meta);
            return waitListButton;
        }

        /**
         * Get cancel button ItemStack
         */
        private @NotNull ItemStack getCancelButton() {
            ItemStack cancelButton = new ItemStack(Material.BARRIER);
            ItemMeta meta = cancelButton.getItemMeta();
            meta.setDisplayName(Main.getConfigs().getMessage("messages.host.gui.main.buttons.cancel.tittle"));
            meta.setLore(Main.getConfigs().getMessages("messages.host.gui.main.buttons.cancel.lore"));
            cancelButton.setItemMeta(meta);
            return cancelButton;
        }
    }
}
