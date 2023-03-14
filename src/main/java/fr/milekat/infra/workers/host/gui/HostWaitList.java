package fr.milekat.infra.workers.host.gui;

import fr.milekat.infra.Main;
import fr.milekat.infra.workers.host.players.PlayersList;
import fr.milekat.infra.workers.utils.Gui;
import fr.milekat.infra.workers.utils.PlayerHead;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HostWaitList {
    private final SmartInventory INVENTORY;

    /**
     * Open a new wait list GUI
     */
    public HostWaitList(Player player, SmartInventory parent) {
        INVENTORY = SmartInventory.builder()
                .id("waitingGui")
                .manager(Main.INVENTORY_MANAGER)
                .provider(new Provider())
                .size(6, 9)
                .title(Main.getConfigs().getMessage("messages.host.gui.wait-list.tittle"))
                .closeable(true)
                .parent(parent)
                .build();
        INVENTORY.open(player);
    }

    private class Provider implements InventoryProvider {
        @Override
        public void init(@NotNull Player player, @NotNull InventoryContents contents) {
            contents.fillBorders(Gui.empty());
            Pagination pagination = contents.pagination();

            updatePages(pagination);
            pagination.setItemsPerPage(28);

            if (!pagination.isFirst()) {
                contents.set(5, 0, ClickableItem.of(
                        PlayerHead.getTextureSkull(PlayerHead.Arrow_Left, 
                                Main.getConfigs().getMessage("messages.general.gui.previous")),
                        e -> INVENTORY.open(player, pagination.previous().getPage())));
            }
            //  Close GUI
            contents.set(5, 4, ClickableItem.of(Gui.getCloseButton(), e ->
                    INVENTORY.getParent().ifPresent(smartInventory -> smartInventory.open(player))));
            if (!pagination.isLast()) {
                contents.set(5, 8, ClickableItem.of(
                        PlayerHead.getTextureSkull(PlayerHead.Arrow_Right, 
                                Main.getConfigs().getMessage("messages.general.gui.next")),
                        e -> INVENTORY.open(player, pagination.next().getPage())));
            }
        }

        @Override
        public void update(Player player, @NotNull InventoryContents contents) {
            updatePages(contents.pagination());
        }

        private void updatePages(@NotNull Pagination pagination) {
            List<ClickableItem> waiters = new ArrayList<>();
            Main.WAIT_LIST.forEach((uuid, username) -> waiters.add(ClickableItem.of(PlayerHead.getPlayerSkull(
                            username, username, 
                            Main.getConfigs().getMessages("messages.host.gui.wait-list.actions")),
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
