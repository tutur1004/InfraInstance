package fr.milekat.hostapi.workers.host.commands;

import fr.milekat.hostapi.Main;
import fr.milekat.hostapi.workers.host.gui.MainGui;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

public class OpenMainGui extends Command {
    protected OpenMainGui() {
        super("host", "Open host main GUI", "/host", Collections.singletonList("h"));
    }

    /**
     * Executes the command, returning its success
     *
     * @param sender       Source object which is executing this command
     * @param commandLabel The alias of the command used
     * @param args         All arguments passed to the command, split via ' '
     * @return true if the command was successful, otherwise false
     */
    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.getUniqueId().equals(Main.HOST_PLAYER.getUniqueId())) {
                new MainGui(player);
            }
        }
        return true;
    }
}
