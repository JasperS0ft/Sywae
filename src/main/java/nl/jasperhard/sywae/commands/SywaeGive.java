package nl.jasperhard.sywae.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import static nl.jasperhard.sywae.items.Syphoner.syphoner;

public class SywaeGive implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use that command!");
            return true;
        }
        Player player = (Player) sender;
        if (cmd.getName().equalsIgnoreCase("sywaegive")) {
            Inventory menu = Bukkit.createInventory(player, 9, "Click on an item to add to your inventory!");
            menu.addItem(syphoner);
            ((Player) sender).openInventory(menu);

        }
         return true;
    }
}
