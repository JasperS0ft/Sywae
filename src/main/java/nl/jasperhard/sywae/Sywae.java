package nl.jasperhard.sywae;

import nl.jasperhard.sywae.commands.SywaeGive;
import nl.jasperhard.sywae.events.SywaeGiveEvents;
import nl.jasperhard.sywae.items.ItemManager;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class Sywae extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        ItemManager.init();

        Bukkit.getServer().getPluginManager().registerEvents(new SywaeGiveEvents(), this);

        getCommand("sywaegive").setExecutor(new SywaeGive());

        System.out.println("Sywae has started!");

    }

    @Override
    public void onDisable() {
        System.out.println("Sywae has stopped!");
    }
}
