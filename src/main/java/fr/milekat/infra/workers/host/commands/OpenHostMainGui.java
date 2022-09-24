package fr.milekat.infra.workers.host.commands;

import fr.milekat.infra.Main;
import fr.milekat.infra.workers.host.gui.HostMainGui;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OpenHostMainGui implements CommandExecutor {
    /**
     * Executes the given command, returning its success
     *
     * @param sender  Source of the command
     * @param command Command which was executed
     * @param label   Alias of the command which was used
     * @param args    Passed command arguments
     * @return true if a valid command, otherwise false
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.getUniqueId().equals(Main.HOST_PLAYER.getUniqueId())) {
                new HostMainGui(player);
            } else {
                sender.sendMessage("§cOnly host can open this GUI.");
            }
        }
        return true;
    }
}
