package fr.milekat.infra.workers.lobby.gui;

import fr.milekat.infra.Main;
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
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

public class LobbyMyHosts {
    private final SmartInventory INVENTORY;

    /**
     * Open a new Main host GUI
     */
    public LobbyMyHosts(Player player, SmartInventory parent) {
        INVENTORY = SmartInventory.builder()
                .id("myHostGui")
                .manager(Main.INVENTORY_MANAGER)
                .provider(new Provider())
                .size(5, 9)
                .title(Main.getConfigs().getMessage("messages.lobby.gui.my-host.tittle"))
                .closeable(true)
                .parent(parent)
                .build();
        INVENTORY.open(player);
    }

    private class Provider implements InventoryProvider, Gui.GuiPages {
        @Override
        public void init(@NotNull Player player, @NotNull InventoryContents contents) {
            //  Default gui pages setup
            Gui.pagesDefault(player, contents, this);

            updatePages(contents, player);
            //  Update pages content
            updatePages(contents, player);
        }

        @Override
        public void update(Player player, @NotNull InventoryContents contents) {
            updatePages(contents, player);
        }

        @Override
        public SmartInventory getInventory() {
            return INVENTORY;
        }

        @Override
        public void updatePages(@NotNull InventoryContents contents, Player player) {
            Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), ()-> {
                try {
                    Map<Integer, ClickableItem> myHosts = new TreeMap<>();
                    Main.getStorage().getActiveInstancesCached()
                            .stream()
                            .filter(instance -> instance.getUser().getUuid().equals(player.getUniqueId()))
                            .filter(instance -> instance.getState().equals(InstanceState.READY))
                            .forEach(instance -> myHosts.put(instance.getId(),
                                    ClickableItem.of(
                                    Gui.getIcon(instance.getGame().getIcon(),
                                            instance.getName(),
                                            Arrays.asList(instance.getDescription(), instance.getMessage())),
                                    event -> {
                                        try {
                                            JoinHandler.serverClick(instance, event.getWhoClicked().getUniqueId(),
                                                    event.getWhoClicked().getName());
                                        } catch (MessagingSendException exception) {
                                            event.getWhoClicked().sendMessage(Main.getConfigs().getMessage(
                                                    "messages.lobby.gui.my-host.messages.join-error"));
                                        }
                                    }))
                            );
                    contents.pagination().setItems(myHosts.values().toArray(new ClickableItem[0]));
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
