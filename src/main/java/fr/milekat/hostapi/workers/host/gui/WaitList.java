package fr.milekat.hostapi.workers.host.gui;

import fr.milekat.hostapi.Main;
import fr.milekat.hostapi.workers.utils.PlayerHead;
import fr.milekat.hostapi.workers.host.players.PlayersList;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WaitList {
    private static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("waitingGui")
            .provider(new WaitList.MainProvider())
            .size(6, 9)
            .title(ChatColor.DARK_AQUA + "Waiting list")
            .closeable(false)
            .build();

    /**
     * Open a new wait list GUI
     */
    public WaitList(Player player) {
        INVENTORY.open(player);
    }

    private static class MainProvider implements InventoryProvider {
        @Override
        public void init(Player player, InventoryContents contents) {
            contents.fillBorders(ClickableItem.empty(new ItemStack(Material.STAINED_GLASS_PANE,
                    0, new Integer(15).shortValue())));
            Pagination pagination = contents.pagination();

            updatePages(pagination);
            pagination.setItemsPerPage(28);

            if (!pagination.isFirst()) {
                contents.set(5, 0, ClickableItem.of(
                        PlayerHead.getTextureSkull(PlayerHead.Arrow_Left, "Previous"),
                        e -> INVENTORY.open(player, pagination.previous().getPage())));
            }
            contents.set(5, 4, ClickableItem.of(new ItemStack(Material.BARRIER), e ->
                    INVENTORY.close(player)));
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
    }
}
