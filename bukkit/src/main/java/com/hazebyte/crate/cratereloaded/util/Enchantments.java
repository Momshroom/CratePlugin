package com.hazebyte.crate.cratereloaded.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringJoiner;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;

public class Enchantments {
    private static final Map<String, Enchantment> ENCHANTS = new HashMap<>();
    private static final StringJoiner enchantSB = new StringJoiner(", ");

    static {
        for (Enchantment e : Enchantment.values()) enchantSB.add(e.getKey().getKey());

        put("environmental_protection", "protection");
        put("protection", "protection");
        put("fire_protection", "fire_protection");
        put("fall_protection", "feather_falling");
        put("explosion_protection", "blast_protection");
        put("projectile_protection", "projectile_protection");
        put("oxygen", "respiration");
        put("water_worker", "aqua_affinity");
        put("thorns", "thorns");
        put("depth_strider", "depth_strider");
        put("frost_walker", "frost_walker");
        put("binding_curse", "binding_curse");
        put("sharpness", "sharpness");
        put("smite", "smite");
        put("bane_of_arthropods", "bane_of_arthropods");
        put("fire_aspect", "fire_aspect");
        put("looting", "looting");
        put("sweeping_edge", "sweeping_edge");
        put("efficiency", "efficiency");
        put("silk_touch", "silk_touch");
        put("unbreaking", "unbreaking");
        put("fortune", "fortune");
        put("power", "power");
        put("punch", "punch");
        put("flame", "flame");
        put("infinity", "infinity");
        put("luck", "luck_of_the_sea");
        put("lure", "lure");
        put("mending", "mending");
        put("vanishing_curse", "vanishing_curse");

        put("channeling", "channeling");
        put("impaling", "impaling");
        put("multishot", "multishot");
        put("piercing", "piercing");
        put("quick_charge", "quick_charge");
    }

    private static void put(String name, String key) {
        Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(key));
        if (enchantment != null) {
            ENCHANTS.put(name, enchantment);
        }
    }

    public static Enchantment getByName(String name) {
        String normalized = name.toLowerCase(Locale.ROOT);
        Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(normalized));
        if (enchantment == null) {
            enchantment = Enchantment.getByName(name.toUpperCase(Locale.ROOT));
        }
        return enchantment == null ? ENCHANTS.get(normalized) : enchantment;
    }

    public static String getStringFormat() {
        return enchantSB.toString();
    }
}
