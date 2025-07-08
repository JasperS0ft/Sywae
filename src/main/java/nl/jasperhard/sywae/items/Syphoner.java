package nl.jasperhard.sywae.items;

import nl.jasperhard.sywae.Sywae;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static nl.jasperhard.sywae.Sywae.ITEM_ID_KEY;

public class Syphoner {

    public static ItemStack syphoner;

    public static void createSyphoner() {
        ItemStack item = new ItemStack(Material.NETHERITE_SWORD, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§6Syphoner");
        List<String> lore = new ArrayList<>();
        lore.add("§7Gear Score: §d100");
        lore.add(" ");
        lore.add("§6Ability: Syphon §a§lATTACK");
        lore.add("§7Heals for §c2 Health §7when hitting a mob.");
        lore.add(" ");
        lore.add("§6Ability: Sweep §a§lRIGHT CLICK");
        lore.add("§7Slash around you dealing §c135% §7melee");
        lore.add("§7damage to all enemies you hit.");
        lore.add("§8Mana Cost: §745");
        lore.add("§8Cooldown: §e1s");
        lore.add(" ");
        lore.add("§6§lLEGENDARY SWORD");
        meta.setLore(lore);
        meta.setUnbreakable(true);
        AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), "Attack Damage", 10, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        AttributeModifier modifier2 = new AttributeModifier(UUID.randomUUID(), "Attack Speed", -2.4, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        meta.addAttributeModifier(Attribute.ATTACK_DAMAGE, modifier);
        meta.addAttributeModifier(Attribute.ATTACK_SPEED, modifier2);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        meta.getPersistentDataContainer().set(Sywae.MANA_ITEM_KEY, PersistentDataType.BYTE, (byte) 1);
        meta.getPersistentDataContainer().set(ITEM_ID_KEY, PersistentDataType.STRING, "syphoner");

        item.setItemMeta(meta);
        syphoner = item;
    }

}
