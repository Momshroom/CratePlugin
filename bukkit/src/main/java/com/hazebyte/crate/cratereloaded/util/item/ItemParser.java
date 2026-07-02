package com.hazebyte.crate.cratereloaded.util.item;

import static com.hazebyte.crate.cratereloaded.util.ConfigConstants.PLUGIN_CUSTOM_MODEL_DATA_FEATURE_NAME;
import static com.hazebyte.crate.cratereloaded.util.ConfigConstants.PLUGIN_NBT_FEATURE_NAME;

import com.hazebyte.crate.api.ServerVersion;
import com.hazebyte.crate.api.util.ItemBuilder;
import com.hazebyte.crate.api.util.ItemHelper;
import com.hazebyte.crate.api.util.Messenger;
import com.hazebyte.crate.api.util.Replacer;
import com.hazebyte.crate.cratereloaded.CorePlugin;
import com.hazebyte.crate.cratereloaded.error.ValidationException;
import com.hazebyte.crate.cratereloaded.serialization.ItemSerialization;
import com.hazebyte.crate.cratereloaded.util.Enchantments;
import com.hazebyte.crate.cratereloaded.util.StringUtils;
import com.hazebyte.crate.cratereloaded.util.format.CustomFormat;
import com.hazebyte.crate.cratereloaded.util.format.Digits;
import com.hazebyte.util.Mat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.regex.Pattern;
import org.bukkit.Color;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class ItemParser {
    private static final Pattern commaSplitPattern = Pattern.compile("[,]");
    private static final Pattern splitPattern = Pattern.compile("[:+',;.]");
    private static PotionType potionType;
    private static PotionEffect potionEffect;
    private static PotionEffectType potionEffectType;
    private static int power;
    private static int duration;
    private static boolean splash;

    private ItemParser() {}

    public static String serialize(ItemStack item) {
        return ItemSerialization.serialize(item);
    }

    /**
     * This returns an ItemStack after parsing the string in a specified format.
     *
     * @param str This string must be a valid format
     * @return an ItemStack
     */
    public static ItemStack parse(String str) {
        if (str == null || str.equals("")) {
            return null;
        }
        str = CustomFormat.format(str);

        // Split the string into it's corresponding parts
        // ["dirt", "name:My_Name"]
        String[] parts = str.split(" ");
        if (parts.length < 2) {
            throw new ValidationException(String.format("The item string is empty: [%s]", str));
        }
        String itemString = parts[0];
        String amountString = parts[1];

        // Maintain compatibility for previous NBT but don't do anything with the data.
        if (itemString.contains("{") && itemString.contains("}")) {
            String previousNbt = itemString.substring(itemString.indexOf("{"), itemString.lastIndexOf("}") + 1);
            itemString = itemString.replace(previousNbt, "");
            Messenger.info(String.format("Found NBT in deprecated format. This will not be parsed: [%s]", previousNbt));
        }
        if (itemString.contains(":")) {
            itemString = itemString.substring(0, itemString.indexOf(":"));
        }

        // This isn't a number so throw and error!
        if (!(StringUtils.isInteger(amountString))) {
            throw new ValidationException(String.format("Item string is missing an amount: [%s].", str));
        }

        // Get the item!
        ItemStack item = ItemUtil.get(itemString);
        if (item == null) {
            throw new ValidationException(String.format("Item stack is invalid: [%s]", str));
        }
        item.setAmount(Integer.parseInt(amountString));

        // modifyItemStack does not appear to copy over the existing item meta when applying the NBT
        // therefore we will need to first apply the NBT.
        java.util.Arrays.stream(parts).skip(2).forEach(part -> parseNbt(item, part));
        // Apply meta
        java.util.Arrays.stream(parts).skip(2).forEach(part -> parseMeta(item, part));
        resetPotionData();

        return item;
    }

    public static boolean isNbtPattern(String meta) {
        return meta.startsWith("{") && meta.endsWith("}");
    }

    public static void parseNbt(ItemStack item, String meta) {
        if (isNbtPattern(meta)) {
            if (!CorePlugin.getPlugin()
                    .getSettings()
                    .isPremiumUserOtherwiseLog(
                            Optional.empty(), String.format("%s - %s", PLUGIN_NBT_FEATURE_NAME, meta))) {
                return;
            }
            try {
                modifyItemStack(item, meta);
            } catch (Exception e) {
                CorePlugin.getPlugin().getLogger().log(Level.FINE, "Unable to apply NBT", e);
            }
        }
    }

    private static void modifyItemStack(ItemStack itemStack, String meta) {
        if (CorePlugin.getPlugin().getServerVersion().gte(ServerVersion.v1_20_R6)) {
            CorePlugin.getPlugin()
                    .getServer()
                    .getUnsafe()
                    .modifyItemStack(
                            itemStack, ItemUtil.getType(itemStack.getType()).orElse("") + meta);
        } else {
            CorePlugin.getPlugin().getServer().getUnsafe().modifyItemStack(itemStack, meta);
        }
    }

    public static void parseMeta(ItemStack item, String meta) {
        final String[] split = splitPattern.split(meta, 2);

        if (isNbtPattern(meta) || split.length < 2) {
            return;
        }

        String key = split[0];
        String value = split[1];
        if (key.equalsIgnoreCase("name")) {
            String displayName = Replacer.replace(value).replace('_', ' ');
            ItemHelper.setName(item, displayName);
        } else if (key.equalsIgnoreCase("lore")) {
            List<String> lore = new ArrayList<>();
            for (String line : value.split("\\|")) {
                String replaced = Replacer.replace(line);
                lore.add(replaced == null ? "" : replaced.replace('_', ' '));
            }
            ItemHelper.setLore(item, lore);
        } else if (key.equalsIgnoreCase("durability")) {
            ItemBuilder.of(item).durability(Short.parseShort(value));
        } else if (key.equalsIgnoreCase("custommodeldata")) {
            if (!CorePlugin.getPlugin()
                    .getSettings()
                    .isPremiumUserOtherwiseLog(Optional.empty(), PLUGIN_CUSTOM_MODEL_DATA_FEATURE_NAME)) {
                return;
            }
            ItemBuilder.of(item).setCustomModelData(Integer.parseInt(value));
        } else if (key.equalsIgnoreCase("unbreakable")) {
            ItemBuilder.of(item).unbreakable(Boolean.parseBoolean(value));
        } else if (key.equalsIgnoreCase("glow")) {
            ItemBuilder.of(item).setGlowing(Boolean.parseBoolean(value));
        } else if (key.equalsIgnoreCase("flag")) {
            addItemFlags(item, value);
        } else if (key.equalsIgnoreCase("hide")) {
            ItemBuilder.of(item).hideAll(Boolean.parseBoolean(value)).asItemStack();
        } else if (key.equalsIgnoreCase("player") && item.getType() == Mat.PLAYER_HEAD.toMaterial()) {
            Messenger.info("Player head retrieval has been deprecated. Please use the skull data.");
        } else if (key.equalsIgnoreCase("skull") && item.getType() == Mat.PLAYER_HEAD.toMaterial()) {
            SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
            try {
                HeadTexture.applyToMeta(skullMeta, value);
            } catch (Exception e) {
                CorePlugin.getPlugin()
                        .getLogger()
                        .info(String.format("There was an error parsing skull data: %s", meta));
            }

            item.setItemMeta(skullMeta);
        } else if (isPotion(item) || isTippedArrow(item)) {
            addPotionEffect(item, meta);
        } else if (key.equalsIgnoreCase("color")) {
            String[] color = value.split("([|,])");
            if (color.length == 3) {
                int red = StringUtils.isDouble(color[0]) ? Integer.parseInt(color[0]) : 0;
                int blue = StringUtils.isDouble(color[1]) ? Integer.parseInt(color[1]) : 0;
                int green = StringUtils.isDouble(color[2]) ? Integer.parseInt(color[2]) : 0;
                LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) item.getItemMeta();
                leatherArmorMeta.setColor(Color.fromRGB(red, blue, green));
                item.setItemMeta(leatherArmorMeta);
            }
        } else {
            parseEnchantments(item, meta);
        }
    }

    private static void addItemFlags(ItemStack item, String flags) {
        ItemMeta meta = item.getItemMeta();
        for (String flag : splitPattern.split(flags)) {
            for (ItemFlag current : ItemFlag.values()) {
                if (flag.equalsIgnoreCase(current.name())) {
                    meta.addItemFlags(current);
                }
            }
        }
        item.setItemMeta(meta);
    }

    private static void addPotionEffect(ItemStack item, String meta) {
        if (!(isPotion(item)) && !(isTippedArrow(item))) {
            return;
        }

        String[] split = splitPattern.split(meta, 2);
        if (split.length < 2) {
            return;
        }

        if (split[0].equalsIgnoreCase("type")) {
            potionType = PotionUtil.getByName(split[1]);
        } else if (split[0].equalsIgnoreCase("effect")) {
            potionEffectType = Potions.getByName(split[1]);
        } else if (split[0].equalsIgnoreCase("power")) {
            if (Digits.containsDigit(split[1])) {
                power = Integer.parseInt(split[1]);
                if (power > 0) {
                    power -= 1;
                }
            }
        } else if (split[0].equalsIgnoreCase("duration")) {
            if (Digits.containsDigit(split[1])) {
                duration = Integer.parseInt(split[1]) * 20;
            }
        } else if (split[0].equalsIgnoreCase("splash")) {
            splash = Boolean.parseBoolean(split[1]);
        }

        if (isValidPotion(item)) {
            PotionMeta potionMeta = (PotionMeta) item.getItemMeta();
            potionEffect =
                    potionEffectType.createEffect((int) (duration / potionEffectType.getDurationModifier()), power);
            potionMeta.addCustomEffect(potionEffect, true);
            item.setItemMeta(potionMeta);

            if (CorePlugin.getPlugin().getServerVersion().gte(ServerVersion.v1_9_R1)) {
                PotionUtil potion = PotionUtil.fromItemStack(item);
                potion.apply(item);
            }
        }
    }

    private static void resetPotionData() {
        potionType = null;
        potionEffect = null;
        potionEffectType = null;
        power = 0;
        duration = 0;
        splash = false;
    }

    private static void parseEnchantments(ItemStack item, String meta) {
        String[] parts = splitPattern.split(meta);
        String enchant = parts[0];
        Enchantment enchantment = Enchantments.getByName(enchant);

        // The enchantment is invalid
        if (enchantment == null) {
            throw new ValidationException(String.format("Enchantment is invalid: [%s]", enchant));
        }

        int level = -1;
        if (parts.length > 1 && StringUtils.isDouble(parts[1])) {
            level = Integer.parseInt(parts[1]);
        }

        addEnchantment(item, enchantment, level);
    }

    private static void addEnchantment(ItemStack item, Enchantment enchantment, int level) {
        if (item.getType() == Mat.ENCHANTED_BOOK.toMaterial()) {
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
            meta.addStoredEnchant(enchantment, level, true);
            item.setItemMeta(meta);
        } else {
            item.addUnsafeEnchantment(enchantment, level);
        }
    }

    private static boolean isShield(ItemStack item) {
        return item.getType().name().contains("SHIELD");
    }

    private static boolean isBanner(ItemStack item) {
        return item.getType().name().contains("BANNER");
    }

    public static boolean isPotion(ItemStack item) {
        return item.getType().name().endsWith("POTION");
    }

    public static boolean isTippedArrow(ItemStack item) {
        return item.getType().name().contains("TIPPED");
    }

    private static boolean isValidPotion(ItemStack item) {
        return (potionType != null) || ((potionEffectType != null) && (duration >= 0));
    }
}
