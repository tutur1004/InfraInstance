package fr.milekat.infra.workers.lobby.gui;

import fr.milekat.infra.Main;
import fr.milekat.infra.api.classes.Game;
import fr.milekat.infra.api.classes.User;
import fr.milekat.infra.messaging.exeptions.MessagingSendException;
import fr.milekat.infra.messaging.sending.MessageToProxy;
import fr.milekat.infra.storage.exeptions.StorageExecuteException;
import fr.milekat.infra.workers.WorkerManager;
import fr.milekat.infra.workers.utils.Gui;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class LobbyCreateHost {
    private final SmartInventory INVENTORY;

    /**
     * Open a new Main host GUI
     */
    public LobbyCreateHost(Player player, SmartInventory parent) {
        INVENTORY = SmartInventory.builder()
                .id("createHostGui")
                .manager(Main.INVENTORY_MANAGER)
                .provider(new Provider())
                .size(5, 9)
                .title(Main.getConfigs().getMessage("messages.lobby.gui.host-create.tittle"))
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
            //  Update pages content
            updatePages(contents, player);
        }

        @Override
        public void update(Player player, @NotNull InventoryContents contents) {}

        @Override
        public SmartInventory getInventory() {
            return INVENTORY;
        }

        // TODO: 28/09/2022 V2: Admin display
        // TODO: 28/09/2022 V2: Update version with new system
        @Override
        public void updatePages(@NotNull InventoryContents contents, Player player) {
            Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), ()-> {
                try {
                    List<Game> games = Main.getStorage().getGamesCached()
                            .stream()
                            .filter(Game::isEnable)
                            .sorted(Comparator.comparingInt(Game::getId))
                            .collect(Collectors.toList());
                    Map<Integer, ClickableItem> availableGames = new TreeMap<>();
                    games.forEach(game ->
                            availableGames.put(game.getId(),
                                    ClickableItem.of(
                                    Gui.getIcon(game.getIcon(), game.getName(), game.getDescriptionSplit()),
                                    event -> {
                                        Player p = (Player) event.getWhoClicked();
                                        try {
                                            User user = Main.getStorage().getUserCache(p.getUniqueId());
                                            if (user!=null && user.getTickets() > 0) {
                                                p.sendMessage(Main.getConfigs().getMessage(
                                                        "messages.lobby.gui.host-create.messages.creation"));
                                                WorkerManager.CREATE_COOL_DOWN.put(player.getUniqueId(), new Date());
                                                MessageToProxy.notifyCreateHost(p.getUniqueId(), game.getId());
                                            } else {
                                                p.sendMessage(Main.getConfigs().getMessage(
                                                        "messages.lobby.gui.host-create.messages.no-ticket"));
                                            }
                                            INVENTORY.getParent().ifPresent(smartInventory ->
                                                    smartInventory.open((Player) event.getWhoClicked()));
                                        } catch (StorageExecuteException | MessagingSendException exception) {
                                            p.sendMessage(Main.getConfigs().getMessage(
                                                    "messages.lobby.gui.host-create.messages.creation-error"));
                                        }
                                    })));
                    contents.pagination().setItems(availableGames.values().toArray(new ClickableItem[0]));
                    Gui.fillPage(contents, 1,1, 3,7);
                    Gui.pagesButtons(player, contents, this);
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
