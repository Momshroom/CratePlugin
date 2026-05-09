package com.hazebyte.crate.cratereloaded.util;

import com.hazebyte.crate.api.crate.Crate;
import com.hazebyte.crate.api.effect.Category;
import com.hazebyte.crate.api.util.ItemBuilder;
import com.hazebyte.util.Mat;
import java.util.regex.Pattern;
import org.bukkit.inventory.ItemStack;

public class ConfigConstants {

    public static final String PLUGIN_NAME = "CrateReloaded";
    public static final String DEFAULT_PLUGIN_PREFIX = String.format("[%s] ", PLUGIN_NAME);
    public static final String CONFIG_FILE_NAME = "config.yml";

    public static boolean SIMPLE_BYPASS = true;

    public static final Pattern UUID_PATTERN = Pattern.compile(
            "^[0-9a-fA-F]{8}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{12}$");

    // Links
    public static final String LINK_CRATE_TYPE = "https://crate.hazebyte.com/docs/config/crate#crate-types";
    public static final String LINK_ANIMATION_TYPE = "https://crate.hazebyte.com/docs/config/crate#animations";
    public static final String LINK_REWARD_TAGS = "https://crate.hazebyte.com/docs/config/crate#tag";

    // Feature
    public static final String PLUGIN_EXPORT_FEATURE_NAME = "export";
    public static final String PLUGIN_NBT_FEATURE_NAME = "nbt";
    public static final String PLUGIN_CUSTOM_MODEL_DATA_FEATURE_NAME = "custom model data";

    // Config
    public static final String CONFIG_SETTINGS_SEARCH_INDEX = "config.yml";
    public static final String CONFIG_LOCATION_STORE_SEARCH_INDEX = "location.yml";
    public static final String CONFIG_CRATE_STORE_SEARCH_INDEX = "crates/";
    public static final String CONFIG_CLAIM_STORE_SEARCH_INDEX = "claim/";
    public static final String CRATE_MATCHER_LEVEL_KEY = "crate.compare";
    public static final String CRATE_ANIMATION_SHUFFLE_DISPLAY_KEY = "crate.display";
    public static final String CRATE_ANIMATION_EXIT_WHILE_SHUFFLING_KEY = "crate.early-animation-exit";
    public static final String CRATE_ANIMATION_CSGO_LENGTH_KEY = "crate.csgo";
    public static final String CRATE_ANIMATION_ROULETTE_LENGTH_KEY = "crate.roulette";
    public static final String CRATE_ANIMATION_WHEEL_LENGTH_KEY = "crate.wheel";
    public static final String CRATE_ANIMATION_CSGO_TOP_ITEM_KEY = "crate.buttons.csgo-top";
    public static final String CRATE_ANIMATION_CSGO_BOTTOM_ITEM_KEY = "crate.buttons.csgo-bot";
    public static final String CRATE_MENU_INTERACTION_KEY = "crate.buttons.enabled";
    public static final String CRATE_MENU_NAME_KEY = "crate.name";
    public static final String CRATE_CONFIRMATION_MENU_NAME_KEY = "crate.confirmation-menu";
    public static final String CRATE_CRAFT_CONTROL_KEY = "crate.craft";
    public static final String CRATE_ITEM_FRAME_CONTROL_KEY = "crate.item-frame";
    public static final String CRATE_HOTBAR_CONTROL_KEY = "crate.hotbar";
    public static final String CRATE_SHULKER_CONTROL_KEY = "crate.shulker";
    public static final String CRATE_PUSHBACK_ENABLED_KEY = "crate.pushback.enabled";
    public static final String CRATE_PUSHBACK_X_MODIFIER_KEY = "crate.pushback.x";
    public static final String CRATE_PUSHBACK_Y_MODIFIER_KEY = "crate.pushback.y";
    public static final String CRATE_PUSHBACK_Z_MODIFIER_KEY = "crate.pushback.z";
    public static final String CRATE_HOLOGRAM_X_MODIFIER_KEY = "crate.hologram.x";
    public static final String CRATE_HOLOGRAM_Y_MODIFIER_KEY = "crate.hologram.y";
    public static final String CRATE_HOLOGRAM_Z_MODIFIER_KEY = "crate.hologram.z";
    public static final String CREATIVE_CONTROL_KEY = "creative.safety";
    public static final String CLAIM_MENU_KEY = "claim.menu";
    public static final String CLAIM_LIMIT_KEY = "claim.limit";
    public static final String CLAIM_HANDLING = "claim.enable";
    public static final String CLAIM_JOIN_MESSAGE_ENABLED_KEY = "claim.reminder.join";
    public static final String CLAIM_ITEM_FORMAT_KEY = "claim.format.item";
    public static final String CLAIM_SUCCESS_MESSAGE_KEY = "claim.format.item-success";
    public static final String CLAIM_ACCEPT_BUTTON_KEY = "crate.buttons.accept";
    public static final String CLAIM_DECLINE_BUTTON_KEY = "crate.buttons.decline";
    public static final String PREVIEW_CLOSE_BUTTON_KEY = "crate.buttons.close";
    public static final String PREVIEW_NEXT_BUTTON_KEY = "crate.buttons.next";
    public static final String PREVIEW_PREVIOUS_BUTTON_KEY = "crate.buttons.back";
    public static final String PLUGIN_ACTION_COOLDOWN_KEY = "cooldown";
    public static final String PLUGIN_PREFIX_KEY = "prefix";
    public static final String PLUGIN_TIMEZONE_KEY = "timezone";
    public static final String PLUGIN_DATE_FORMAT_KEY = "date-format";
    public static final String PLUGIN_LOGGER_ENABLE_KEY = "logger.enable";
    public static final String PLUGIN_LOGGER_LEVEL_KEY = "logger.level";
    public static final String PLUGIN_DECIMAL_FORMAT_KEY = "decimal-format";
    public static final String PLUGIN_HOLOGRAM_PREFERENCE_KEY = "hologram-plugin";

    public static final ItemStack WHITE_PANE_GLASS = new ItemStack(Mat.WHITE_STAINED_GLASS_PANE.toMaterial());
    public static final ItemStack ANIMATION_REWARD_INDICATOR_ITEM = new ItemBuilder(Mat.REDSTONE_TORCH.toMaterial())
            .displayName("&6Prize!")
            .asItemStack();

    public static final ItemStack NAVIGATION_NEXT_CRATE_BUTTON = new ItemBuilder(
                    Mat.LIME_STAINED_GLASS_PANE.toMaterial())
            .displayName("&aNext_Page")
            .lore("&eClick to enter the next page.")
            .asItemStack();
    public static final ItemStack NAVIGATION_PREVIOUS_BUTTON_ITEM = new ItemBuilder(Mat.RED_BED.toMaterial())
            .displayName("&6Previous Page.")
            .lore("&eClick to go back to the previous page.")
            .asItemStack();

    public static final ItemStack NAVIGATION_OPEN_CRATE_BUTTON = new ItemBuilder(
                    Mat.LIME_STAINED_GLASS_PANE.toMaterial())
            .displayName("&aYes")
            .lore("&fClick here to open {crate}!")
            .asItemStack();

    public static final ItemStack NAVIGATION_CLOSE_CRATE_BUTTON = new ItemBuilder(Mat.MUSIC_DISC_11.toMaterial())
            .displayName("&4Close!")
            .asItemStack();

    public static final ItemStack NAVIGATION_DECLINE_OPEN_CRATE_BUTTON = new ItemBuilder(
                    Mat.LIME_STAINED_GLASS_PANE.toMaterial())
            .displayName("&4No")
            .asItemStack();

    public static String generateCrateEffectKey(Crate crate, Category category, String effectKey) {
        return "CrateEffectIdSupplier{"
                + "crate="
                + crate
                + ", category="
                + category
                + ", effectKey='"
                + effectKey
                + '\''
                + '}';
    }

    /**
     * Generates a unique effect key using crate name instead of crate object.
     * This is used for CrateV2 during parsing when the full object may not be available yet.
     *
     * @param crateName The name of the crate
     * @param category The effect category
     * @param effectKey The effect configuration key
     * @return A unique identifier for this effect configuration
     */
    public static String generateCrateEffectKeyV2(String crateName, Category category, String effectKey) {
        return "CrateEffectIdSupplier{"
                + "crate=CrateV2(crateName="
                + crateName
                + ")"
                + ", category="
                + category
                + ", effectKey='"
                + effectKey
                + '\''
                + '}';
    }
}
