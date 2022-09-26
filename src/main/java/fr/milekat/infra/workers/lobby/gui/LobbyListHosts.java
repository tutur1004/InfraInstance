package fr.milekat.infra.workers.lobby.gui;

import fr.milekat.infra.Main;
import fr.milekat.infra.api.classes.AccessStates;
import fr.milekat.infra.api.classes.InstanceState;
import fr.milekat.infra.messaging.exeptions.MessagingSendException;
import fr.milekat.infra.storage.exeptions.StorageExecuteException;
import fr.milekat.infra.workers.utils.Gui;
import fr.milekat.infra.workers.utils.JoinHandler;
import fr.milekat.infra.workers.utils.PlayerHead;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LobbyListHosts {
    private static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("hostListGui")
            .manager(Main.INVENTORY_MANAGER)
            .provider(new LobbyListHosts.HostListProvider())
            .size(5, 9)
            .title(ChatColor.DARK_AQUA + "Available hosts")
            .closeable(true)
            .parent(LobbyMainGui.INVENTORY)
            .build();

    /**
     * Open a new wait list GUI
     */
    public LobbyListHosts(Player player) {
        INVENTORY.open(player);
    }

    private static class HostListProvider implements InventoryProvider {
        @Override
        public void init(@NotNull Player player, @NotNull InventoryContents contents) {
            contents.fillBorders(Gui.empty());
            Pagination pagination = contents.pagination();

            updatePages(contents);
            pagination.setItemsPerPage(28);

            //  Player infos
            contents.set(0, 4, ClickableItem.empty(
                    PlayerHead.getPlayerSkull(player.getName(), "Your stats", Gui.getPlayerStats(player))));

            if (!pagination.isFirst()) {
                contents.set(4, 0, ClickableItem.of(
                        PlayerHead.getTextureSkull(PlayerHead.Arrow_Left, "Previous"),
                        e -> INVENTORY.open(player, pagination.previous().getPage())));
            }
            //  Close GUI
            contents.set(4, 4, ClickableItem.of(Gui.getCloseButton(), e ->
                    INVENTORY.getParent().ifPresent(smartInventory -> smartInventory.open(player))));
            if (!pagination.isLast()) {
                contents.set(4, 8, ClickableItem.of(
                        PlayerHead.getTextureSkull(PlayerHead.Arrow_Right, "Next"),
                        e -> INVENTORY.open(player, pagination.next().getPage())));
            }
        }

        @Override
        public void update(Player player, @NotNull InventoryContents contents) {
            updatePages(contents);
        }

        private void updatePages(@NotNull InventoryContents contents) {
            Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), ()-> {
                try {
                    List<ClickableItem> availableHosts = new ArrayList<>();
                    Main.getStorage().getActiveInstancesCached()
                            .stream()
                            .filter(instance -> !instance.getAccess().equals(AccessStates.PRIVATE))
                            .filter(instance -> instance.getState().equals(InstanceState.READY))
                            .forEach(instance -> availableHosts.add(ClickableItem.of(
                                    Gui.getIcon(instance.getAccess(),
                                            instance.getName(),
                                            Arrays.asList(instance.getDescription(), instance.getMessage())),
                                    event -> {
                                        try {
                                            JoinHandler.serverClick(instance, event.getWhoClicked().getUniqueId(),
                                                    event.getWhoClicked().getName());
                                        } catch (MessagingSendException exception) {
                                            event.getWhoClicked().sendMessage("§cError, please try again.");
                                        }
                                    }))
                            );
                    contents.pagination().setItems(availableHosts.toArray(new ClickableItem[0]));
                    Gui.fillPage(contents, 1,1, 3,7, availableHosts);
                } catch (StorageExecuteException exception) {
                    if (Main.DEBUG) {
                        Main.getOwnLogger().info("Error while trying to load instances in Storage.");
                        exception.printStackTrace();
                    }
                }
            });
        }
    }
}
