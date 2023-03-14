package fr.milekat.infra.workers.lobby.gui;

import fr.milekat.infra.Main;
import fr.milekat.infra.api.classes.AccessStates;
import fr.milekat.infra.api.classes.InstanceState;
import fr.milekat.infra.messaging.exeptions.MessagingSendException;
import fr.milekat.infra.storage.exeptions.StorageExecuteException;
import fr.milekat.infra.workers.utils.Gui;
import fr.milekat.infra.workers.utils.JoinHandler;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

public class LobbyListHosts {
    private final SmartInventory INVENTORY;

    /**
     * Open a new wait list GUI
     */
    public LobbyListHosts(Player player, SmartInventory parent) {
        INVENTORY = SmartInventory.builder()
                .id("hostListGui")
                .manager(Main.INVENTORY_MANAGER)
                .provider(new HostListProvider())
                .size(5, 9)
                .title(ChatColor.DARK_AQUA + "Available hosts")
                .closeable(true)
                .parent(parent)
                .build();
        INVENTORY.open(player);
    }

    private class HostListProvider implements InventoryProvider, Gui.GuiPages {
        @Override
        public void init(@NotNull Player player, @NotNull InventoryContents contents) {
            //  Default gui pages setup
            Gui.pagesDefault(player, contents, this);
            //  Update pages content
            updatePages(contents, player);
        }

        @Override
        public void update(Player player, @NotNull InventoryContents contents) {
            updatePages(contents, player);
        }

        @Contract(value = " -> new", pure = true)
        @Override
        public @NotNull SmartInventory getInventory() {
            return INVENTORY;
        }

        @Override
        public void updatePages(@NotNull InventoryContents contents, Player player) {
            Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), ()-> {
                try {
                    Map<Integer, ClickableItem> availableHosts = new TreeMap<>();
                    Main.getStorage().getActiveInstancesCached()
                            .stream()
                            .filter(instance -> !instance.getAccess().equals(AccessStates.PRIVATE))
                            .filter(instance -> instance.getState().equals(InstanceState.READY))
                            .forEach(instance -> availableHosts.put(instance.getId(),
                                    ClickableItem.of(
                                    Gui.getIcon(instance.getAccess(),
                                            instance.getName(),
                                            Arrays.asList(instance.getDescription(), instance.getMessage())),
                                    event -> {
                                        try {
                                            JoinHandler.serverClick(instance, event.getWhoClicked().getUniqueId(),
                                                    event.getWhoClicked().getName());
                                        } catch (MessagingSendException exception) {
                                            event.getWhoClicked().sendMessage(Main.getConfigs()
                                                    .getMessage("messages.general.error",
                                                            "&cServer error, please contact staff."));
                                        }
                                    }))
                            );
                    contents.pagination().setItems(availableHosts.values().toArray(new ClickableItem[0]));
                    Gui.fillPage(contents, 1,1, 3,7);
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
