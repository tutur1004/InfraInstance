package fr.milekat.infra.workers.lobby.gui;

import fr.milekat.infra.Main;
import fr.milekat.infra.api.classes.Game;
import fr.milekat.infra.api.classes.User;
import fr.milekat.infra.messaging.exeptions.MessagingSendException;
import fr.milekat.infra.messaging.sending.MessageToProxy;
import fr.milekat.infra.storage.exeptions.StorageExecuteException;
import fr.milekat.infra.workers.utils.Gui;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LobbyCreateHost {
    private static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("createHostGui")
            .manager(Main.INVENTORY_MANAGER)
            .provider(new Provider())
            .size(5, 9)
            .title(ChatColor.DARK_AQUA + "Create a new Host - Select a game")
            .closeable(true)
            .parent(LobbyMainGui.INVENTORY)
            .build();

    /**
     * Open a new Main host GUI
     */
    public LobbyCreateHost(Player player) {
        INVENTORY.open(player);
    }

    private static class Provider implements InventoryProvider {
        @Override
        public void init(@NotNull Player player, @NotNull InventoryContents contents) {
            contents.fillBorders(Gui.empty());
            Pagination pagination = contents.pagination();

            updatePages(contents);
            pagination.setItemsPerPage(28);

            //  Player infos
            contents.set(0, 4, ClickableItem.empty(
                    PlayerHead.getPlayerSkull(player.getName(), "Your stats", Gui.getPlayerStats(player))));
            //  Close GUI
            contents.set(4, 4, ClickableItem.of(Gui.getCloseButton(), e ->
                    INVENTORY.getParent().ifPresent(smartInventory -> smartInventory.open(player))));
        }

        @Override
        public void update(Player player, @NotNull InventoryContents contents) {}

        // TODO: 28/09/2022 V2: Admin display
        // TODO: 28/09/2022 V2: Update version with new system
        private void updatePages(@NotNull InventoryContents contents) {
            Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), ()-> {
                try {
                    Map<String, Game> games = new HashMap<>();
                    Main.getStorage().getGamesCached()
                            .stream()
                            .filter(Game::isEnable)
                            .forEach(game -> games.put(game.getName(), game));
                    List<ClickableItem> availableGames = new ArrayList<>();
                    games.values().forEach(game ->
                            availableGames.add(ClickableItem.of(
                                    Gui.getIcon(game.getIcon(), game.getName(), game.getDescriptionSplit()),
                                    event -> {
                                        Player player = (Player) event.getWhoClicked();
                                        try {
                                            User user = Main.getStorage().getUserCache(player.getUniqueId());
                                            if (user!=null && user.getTickets() > 0) {
                                                player.sendMessage("§aHost request received, trying to create host...");
                                                MessageToProxy.notifyCreateHost(player.getUniqueId(), game.getId());
                                            } else {
                                                player.sendMessage("§cNot enough tickets !");
                                            }
                                            INVENTORY.getParent().ifPresent(smartInventory ->
                                                    smartInventory.open((Player) event.getWhoClicked()));
                                        } catch (StorageExecuteException | MessagingSendException exception) {
                                            player.sendMessage("§cError, please try again.");
                                        }
                                    })));
                    contents.pagination().setItems(availableGames.toArray(new ClickableItem[0]));
                    Gui.fillPage(contents, 1,1, 3,7, availableGames);
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
