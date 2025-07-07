package nl.jasperhard.sywae;

import net.md_5.bungee.api.ChatMessageType;
import nl.jasperhard.sywae.commands.SywaeGive;
import nl.jasperhard.sywae.events.SywaeGiveEvents;
import nl.jasperhard.sywae.items.ItemManager;
import nl.jasperhard.sywae.mana.ManaManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public final class Sywae extends JavaPlugin implements Listener {

    public static NamespacedKey MANA_ITEM_KEY;

    private ManaManager manaManager;

    @Override
    public void onEnable() {
        MANA_ITEM_KEY = new NamespacedKey(this, "uses_mana");

        manaManager = new ManaManager(this);

        startManaDisplayTask();

        ItemManager.init();

        Bukkit.getServer().getPluginManager().registerEvents(new SywaeGiveEvents(), this);

        getCommand("sywaegive").setExecutor(new SywaeGive());

        System.out.println("Sywae has started!");

    }

    @Override
    public void onDisable() {
        manaManager.saveManaData();

        System.out.println("Sywae has stopped!");
    }

    private boolean isManaItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()){ return false;}

        ItemMeta meta = item.getItemMeta();
        NamespacedKey manaItemKey = new NamespacedKey(this, "uses_mana");

        Byte tag = meta.getPersistentDataContainer().get(manaItemKey, PersistentDataType.BYTE);
        return tag != null && tag == (byte) 1;
    }

    private void startManaDisplayTask() {
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                ItemStack item = player.getInventory().getItemInMainHand();
                if (!isManaItem(item)) continue;

                int currentMana = manaManager.getMana(player);
                int maxMana = manaManager.getMaxMana(player);

                int regen = (int) Math.ceil(maxMana * 0.1);
                currentMana = Math.min(currentMana + regen, maxMana);
                manaManager.setMana(player, currentMana);

                String actionBarMessage = ChatColor.AQUA + "✎ Mana " +
                        ChatColor.WHITE + currentMana +
                        ChatColor.GRAY + "/" +
                        ChatColor.WHITE + maxMana;

                player.spigot().sendMessage(
                        ChatMessageType.ACTION_BAR,
                        new net.md_5.bungee.api.chat.TextComponent(actionBarMessage)
                );
            }
        }, 0L, 10L);
    }

    public ManaManager getManaManager() {
        return manaManager;
    }
}
