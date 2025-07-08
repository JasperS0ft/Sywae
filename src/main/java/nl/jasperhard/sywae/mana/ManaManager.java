package nl.jasperhard.sywae.mana;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ManaManager {
    private final Map<UUID, Integer> manaMap = new HashMap<>();
    private final Map<UUID, Integer> maxManaMap = new HashMap<>();
    private final JavaPlugin plugin;
    private final File manaFile;
    private FileConfiguration manaConfig;

    private static final Map<UUID, BukkitTask> tasks = new HashMap<>();

    public static void sendActionBarMessage(Player player, String message, JavaPlugin plugin, int durationTicks) {
        UUID uuid = player.getUniqueId();

        if (tasks.containsKey(uuid)) {tasks.get(uuid).cancel();}

        BukkitTask task = plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
            int ticksPassed = 0;

            @Override
            public void run() {
                if (ticksPassed >= durationTicks) {
                    tasks.remove(uuid);
                    cancel();
                    return;
                }
                String msg = message;
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(msg));
                ticksPassed += 1;
            }
            private void cancel() {
                tasks.remove(uuid);
            }
        }, 0L, 1L);
        tasks.put(uuid, task);
    }

    public ManaManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.manaFile = new File(plugin.getDataFolder(), "mana.yml");
        this.manaConfig = YamlConfiguration.loadConfiguration(manaFile);
        loadManaData();
    }

    public void setMana(Player player, int amount) {
        UUID uuid = player.getUniqueId();
        int max = getMaxMana(player);
        manaMap.put(uuid, Math.max(0, Math.min(amount, max)));
    }

    public void addMana(Player player, int amount) {
        setMana(player, getMana(player) + amount);
    }

    public void subtractMana(Player player, int amount) {
        setMana(player, getMana(player) - amount);
    }

    public int getMana(Player player) {
        return manaMap.getOrDefault(player.getUniqueId(), getMaxMana(player));
    }

    public void setMaxmana(Player player, int amount) {
        UUID uuid = player.getUniqueId();
        maxManaMap.put(uuid, Math.max(0, amount));
        if (getMana(player) > amount) {
            setMana(player, amount);
        }
    }

    public int getMaxMana(Player player) {
        return maxManaMap.getOrDefault(player.getUniqueId(), 100);
    }

    public void reset(Player player) {
        UUID uuid = player.getUniqueId();
        maxManaMap.putIfAbsent(uuid, 100);
        manaMap.put(uuid, getMaxMana(player));
    }

    public void saveManaData() {
        for (UUID uuid : manaMap.keySet()) {
            String id = uuid.toString();
            manaConfig.set(id + ".mana", manaMap.get(uuid));
            manaConfig.set(id + ".maxMana", maxManaMap.getOrDefault(uuid, 100));
        } try {
            manaConfig.save(manaFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadManaData() {
        for (String key : manaConfig.getKeys(false)) {
            UUID uuid = UUID.fromString(key);
            int mana = manaConfig.getInt(key + ".mana", 100);
            int maxMana = manaConfig.getInt(key + ".maxMana", 100);
            manaMap.put(uuid, mana);
            maxManaMap.put(uuid, maxMana);
        }
    }
}
