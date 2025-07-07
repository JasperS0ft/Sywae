package nl.jasperhard.sywae.events;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class SywaeGiveEvents implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getView().getTitle().equalsIgnoreCase("Click on an item to add to your inventory!")) {
            Player p = (Player) e.getWhoClicked();
            if (e.getCurrentItem() != null) {
                if (e.getCurrentItem().getItemMeta().isUnbreakable()) {
                    p.getInventory().addItem(e.getCurrentItem());
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 2);
                    p.sendMessage("§f<Sywae> " + e.getCurrentItem().getItemMeta().getDisplayName() + "§f has been added to your inventory!");
                }
            }
            e.setCancelled(true);
        }
    }
}
