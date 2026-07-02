package com.hazebyte.crate.cratereloaded.util.item;

import com.hazebyte.crate.cratereloaded.util.format.Digits;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.bukkit.NamespacedKey;
import org.bukkit.potion.PotionEffectType;

public class Potions {
    private static final Map<String, PotionEffectType> POTIONS = new HashMap<>();

    static {
        put("absorption", "absorption");
        put("blindness", "blindness");
        put("confusion", "nausea");
        put("nausea", "nausea");
        put("resistance", "resistance");
        put("haste", "haste");
        put("fire_resistance", "fire_resistance");
        put("harm", "instant_damage");
        put("instant_damage", "instant_damage");
        put("heal", "instant_health");
        put("instant_health", "instant_health");
        put("health_boost", "health_boost");
        put("hunger", "hunger");
        put("strength", "strength");
        put("invisibility", "invisibility");
        put("jump", "jump_boost");
        put("jump_boost", "jump_boost");
        put("levitation", "levitation");
        put("luck", "luck");
        put("unluck", "unluck");
        put("night_vision", "night_vision");
        put("poison", "poison");
        put("regeneration", "regeneration");
        put("saturation", "saturation");
        put("slowness", "slowness");
        put("fatigue", "mining_fatigue");
        put("mining_fatigue", "mining_fatigue");
        put("slow_falling", "slow_falling");
        put("conduit_power", "conduit_power");
        put("dolphins_grace", "dolphins_grace");
        put("speed", "speed");
        put("water_breath", "water_breathing");
        put("weakness", "weakness");
        put("wither", "wither");
    }

    private static void put(String name, String key) {
        PotionEffectType type = PotionEffectType.getByKey(NamespacedKey.minecraft(key));
        if (type != null) {
            POTIONS.put(name, type);
        }
    }

    public static PotionEffectType getByName(String string) {
        PotionEffectType potionEffectType;
        if (Digits.containsDigit(string)) {
            potionEffectType = PotionEffectType.getById(Integer.parseInt(string));
        } else {
            String normalized = string.toLowerCase(Locale.ROOT);
            potionEffectType = PotionEffectType.getByKey(NamespacedKey.minecraft(normalized));
            if (potionEffectType == null) {
                potionEffectType = PotionEffectType.getByName(string.toUpperCase(Locale.ROOT));
            }
        }

        if (potionEffectType == null) {
            potionEffectType = POTIONS.get(string.toLowerCase(Locale.ROOT));
        }
        return potionEffectType;
    }
}
