package fr.milekat.infra.workers.host.gui;

import fr.milekat.infra.Main;
import fr.milekat.infra.messaging.exeptions.MessagingSendException;
import fr.milekat.infra.workers.host.messaging.HostProxySend;
import fr.milekat.infra.workers.host.players.PlayersList;
import fr.milekat.infra.workers.utils.PlayerHead;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WhiteList  {
    private static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("whitelistGui")
            .provider(new WhiteList.MainProvider())
            .size(6, 9)
            .title(ChatColor.DARK_AQUA + "Whitelisted players")
            .closeable(false)
            .build();

    /**
     * Open a new whitelist GUI
     */
    public WhiteList(Player player) {
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

            contents.set(0, 4, ClickableItem.of(getAnvilButton(), e -> openAnvilGui(player)));
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
            Main.WHITE_LIST.forEach((uuid, username) -> waiters.add(ClickableItem.of(PlayerHead.getPlayerSkull(
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

        private ItemStack getAnvilButton() {
            ItemStack anvilButton = new ItemStack(Material.ANVIL);
            ItemMeta meta = anvilButton.getItemMeta();
            meta.setDisplayName("Invite a player");
            anvilButton.setItemMeta(meta);
            return anvilButton;
        }

        private void openAnvilGui(Player guiPlayer) {
            new AnvilGUI.Builder()
                    .onClose(INVENTORY::open)
                    .onComplete((player, text) -> {
                        try {
                            HostProxySend.notifyInvitePlayer(guiPlayer, text);
                        } catch (MessagingSendException exception) {
                            guiPlayer.sendMessage("§cServer error, please contact the staff.");
                            exception.printStackTrace();
                        }
                        return AnvilGUI.Response.close();
                    })
                    .text("Name of player")
                    .itemLeft(new ItemStack(Material.NAME_TAG))
                    .plugin(Main.getInstance())
                    .open(guiPlayer);
        }
    }
}
