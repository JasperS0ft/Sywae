package nl.jasperhard.sywae.events;

import net.md_5.bungee.api.ChatMessageType;
import nl.jasperhard.sywae.Sywae;
import nl.jasperhard.sywae.mana.ManaManager;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static nl.jasperhard.sywae.Sywae.ITEM_ID_KEY;

public class SyphonerEvents implements Listener {

    private final Sywae plugin;
    private final ManaManager manaManager;

    public SyphonerEvents(Sywae plugin, ManaManager manaManager) {
        this.plugin = plugin;
        this.manaManager = manaManager;
    }

    private boolean isSyphoner(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {return false;}

        String id = item.getItemMeta().getPersistentDataContainer().get(ITEM_ID_KEY, PersistentDataType.STRING);

        return "syphoner".equals(id);
    }

    private final Map<UUID, Long> slashCooldowns = new HashMap<>();
    private final long COOLDOWN_MS = 1000;

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        Action action = e.getAction();
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            Player player = e.getPlayer();
            if (isSyphoner(player.getInventory().getItemInMainHand())) {
                long now = System.currentTimeMillis();
                if (slashCooldowns.containsKey(player.getUniqueId())) {
                    long lastUsed = slashCooldowns.get(player.getUniqueId());
                    if (now - lastUsed < COOLDOWN_MS) return;
                }

                slashCooldowns.put(player.getUniqueId(), now);
                performSlash(player);
            }
        }
    }

    private void performSlash(Player player) {
        int cost = 35;

        if (manaManager.getMana(player) < cost) {
            player.spigot().sendMessage(
                    ChatMessageType.ACTION_BAR,
                    new net.md_5.bungee.api.chat.TextComponent(ChatColor.RED.toString() + ChatColor.BOLD + "NOT ENOUGH MANA")
            );
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_TELEPORT, 1f, 0.5f);
            return;
        }

        manaManager.setMana(player, manaManager.getMana(player) - cost);

        double baseDamage = getMeleeDamage(player);
        double damage = baseDamage * 1.5;
        double radius = 4.0;

        Location eye = player.getEyeLocation();
        Vector direction = eye.getDirection().normalize();

        for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
            if (entity instanceof LivingEntity target && !entity.equals(player)) {

                Vector toEntity = target.getEyeLocation().toVector().subtract(eye.toVector()).normalize();
                double angle = direction.angle(toEntity);
                if (angle <= Math.toRadians(50)) {
                    target.damage(damage, player);
                }
            }
        }
        Location particleLocation = eye.clone().add(direction.multiply(1.5)).subtract(0, 0.5, 0);
        player.swingMainHand();
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1f, 1f);
        playSlashParticles(player);
    }

    private void playSlashParticles(Player player) {
        Location origin = player.getEyeLocation().subtract(0, 0.5, 0);
        Vector direction = origin.getDirection().normalize();
        World world = player.getWorld();

        double radius = 4;
        int particles = 30;
        double arcDegrees = 100;

        for (int i = -particles / 2; i < particles / 2; i++) {
            double angle = Math.toRadians(i * (arcDegrees / particles));
            Vector rotated = direction.clone().rotateAroundY(angle).multiply(radius);
            Location particleLoc = origin.clone().add(rotated);

            world.spawnParticle(Particle.SWEEP_ATTACK, particleLoc, 1, 0, 0, 0, 0);
        }
    }

    private double getMeleeDamage(Player player) {
        ItemStack weapon = player.getInventory().getItemInMainHand();
        if (weapon == null || weapon.getType().isAir()) return 1;

        double baseDamage = 10;

        ItemMeta meta = weapon.getItemMeta();
        if (meta != null && meta.hasEnchant(Enchantment.SHARPNESS)) {
            int level = meta.getEnchantLevel(Enchantment.SHARPNESS);
            baseDamage += 1.0 + (level * 0.5);
        }
        return baseDamage;
    }

    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player player)) return;
        if (isSyphoner(player.getInventory().getItemInMainHand())) {
            double maxHealth = player.getAttribute(Attribute.MAX_HEALTH).getValue();
            double healAmount = 0.25;
            double newHealth = Math.min(player.getHealth() + healAmount, maxHealth);
            player.setHealth(newHealth);
        }
    }
}
