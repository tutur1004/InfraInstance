package fr.milekat.infra.workers.lobby.gui;

import fr.milekat.infra.Main;
import fr.milekat.infra.workers.WorkerManager;
import fr.milekat.infra.workers.utils.Gui;
import fr.milekat.infra.workers.utils.PlayerHead;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class LobbyMainGui  {
    public final SmartInventory INVENTORY;

    /**
     * Open a new Main lobby GUI
     */
    public LobbyMainGui(@NotNull Player player) {
        INVENTORY = SmartInventory.builder()
                .id("mainGui")
                .manager(Main.INVENTORY_MANAGER)
                .provider(new Provider())
                .size(5, 9)
                .title(Main.getConfigs().getMessage("messages.lobby.gui.main.tittle"))
                .closeable(true)
                .build();
        INVENTORY.open(player);
    }

    /**
     * Open a new Main lobby GUI
     */
    public LobbyMainGui(@NotNull Player player, @NotNull SmartInventory parent) {
        INVENTORY = SmartInventory.builder()
                .id("mainGui")
                .manager(Main.INVENTORY_MANAGER)
                .provider(new Provider())
                .size(5, 9)
                .title(Main.getConfigs().getMessage("messages.lobby.gui.main.tittle"))
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
            contents.set(4,8, empty);
            //  Player infos
            Gui.setPlayerStats(player, contents, 0, 4);
            //  Open MyHosts GUI
            contents.set(2, 2, ClickableItem.of(getMyHostButton(player.getName()),
                    e -> new LobbyMyHosts(player, INVENTORY)));
            //  Open CreateHost GUI
            contents.set(2, 4, ClickableItem.of(getCreateHostButton(), e -> {
                Optional<Map.Entry<UUID, Date>> present = WorkerManager.CREATE_COOL_DOWN.entrySet()
                        .stream()
                        .filter(entry -> entry.getKey().equals(player.getUniqueId()))
                        .filter(entry -> entry.getValue().getTime() + WorkerManager.CREATE_DELAY > new Date().getTime())
                        .findFirst();
                if (!present.isPresent()) {
                    WorkerManager.CREATE_COOL_DOWN.remove(player.getUniqueId());
                    new LobbyCreateHost(player, INVENTORY);
                } else {
                    player.sendMessage(Main.getConfigs()
                            .getMessage("messages.lobby.gui.main.buttons.my-host.tittle")
                            .replaceAll("<time-reaming>", String.valueOf((-1 * (
                                    new Date().getTime() - WorkerManager.CREATE_DELAY -
                                            WorkerManager
                                                    .CREATE_COOL_DOWN
                                                    .get(player.getUniqueId())
                                                    .getTime()) / 1000)
                                    )
                            )
                    );
                }
            }));
            //  Open HostList GUI
            contents.set(2, 6, ClickableItem.of(getPublicHostsButton(),
                    e -> new LobbyListHosts(player, INVENTORY)));
            //  Close GUI
            contents.set(4, 4, ClickableItem.of(Gui.getCloseButton(), e -> INVENTORY.close(player)));
        }

        @Override
        public void update(@NotNull Player player, @NotNull InventoryContents contents) {}

        private @NotNull ItemStack getMyHostButton(String name) {
            return PlayerHead.getPlayerSkull(name,
                    Main.getConfigs().getMessage("messages.lobby.gui.main.buttons.my-host.tittle"),
                    Main.getConfigs().getMessages("messages.lobby.gui.main.buttons.my-host.lore"));
        }

        private @NotNull ItemStack getCreateHostButton() {
            ItemStack itemStack = new ItemStack(Material.GRASS);
            ItemMeta meta = itemStack.getItemMeta();
            meta.setDisplayName(Main.getConfigs().getMessage(
                    "messages.lobby.gui.main.buttons.create-host.tittle"));
            meta.setLore(Main.getConfigs().getMessages(
                    "messages.lobby.gui.main.buttons.create-host.lore"));
            itemStack.setItemMeta(meta);
            return itemStack;
        }

        private @NotNull ItemStack getPublicHostsButton() {
            return PlayerHead.getTextureSkull(PlayerHead.Earth,
                    Main.getConfigs().getMessage("messages.lobby.gui.main.buttons.public-hosts.tittle"),
                    Main.getConfigs().getMessages("messages.lobby.gui.main.buttons.public-hosts.lore"));
        }
    }
}
