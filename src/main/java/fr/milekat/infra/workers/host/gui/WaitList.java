package fr.milekat.infra.workers.host.gui;

import fr.milekat.infra.Main;
import fr.milekat.infra.workers.host.players.PlayersList;
import fr.milekat.infra.workers.utils.PlayerHead;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WaitList {
    private static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("waitingGui")
            .manager(Main.INVENTORY_MANAGER)
            .provider(new WaitList.MainProvider())
            .size(6, 9)
            .title(ChatColor.DARK_AQUA + "Waiting list")
            .closeable(true)
            .build();

    /**
     * Open a new wait list GUI
     */
    public WaitList(Player player) {
        INVENTORY.open(player);
    }

    private static class MainProvider implements InventoryProvider {
        @Override
        public void init(@NotNull Player player, @NotNull InventoryContents contents) {
            contents.fillBorders(MainGui.empty());
            Pagination pagination = contents.pagination();

            updatePages(pagination);
            pagination.setItemsPerPage(28);

            if (!pagination.isFirst()) {
                contents.set(5, 0, ClickableItem.of(
                        PlayerHead.getTextureSkull(PlayerHead.Arrow_Left, "Previous"),
                        e -> INVENTORY.open(player, pagination.previous().getPage())));
            }
            //  Close GUI
            contents.set(5, 4, ClickableItem.of(getCloseButton(), e -> INVENTORY.close(player)));
            if (!pagination.isLast()) {
                contents.set(5, 8, ClickableItem.of(
                        PlayerHead.getTextureSkull(PlayerHead.Arrow_Right, "Next"),
                        e -> INVENTORY.open(player, pagination.next().getPage())));
            }
        }

        @Override
        public void update(Player player, InventoryContents contents) {
            updatePages(contents.pagination());
        }

        private void updatePages(Pagination pagination) {
            List<ClickableItem> waiters = new ArrayList<>();
            Main.WAIT_LIST.forEach((uuid, username) -> waiters.add(ClickableItem.of(PlayerHead.getPlayerSkull(
                            username, username, Arrays.asList("Left click", "Add Whitelist", "Right click", "Remove")),
                    e -> {
                        if (e.isLeftClick()) {
                            PlayersList.addPlayerToWhiteList(uuid, username);
                        } else if (e.isRightClick()) {
                            PlayersList.removePlayerFromWaitList(uuid);
                        }
                    })
            ));
            pagination.setItems(waiters.toArray(new ClickableItem[0]));
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
    }
}
