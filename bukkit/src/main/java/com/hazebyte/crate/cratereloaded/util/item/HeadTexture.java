package com.hazebyte.crate.cratereloaded.util.item;

import com.hazebyte.crate.api.ServerVersion;
import com.hazebyte.crate.api.util.Messenger;
import com.hazebyte.crate.cratereloaded.CorePlugin;
import com.google.common.collect.HashMultimap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.UUID;
import java.util.logging.Level;
import lombok.NonNull;
import org.bukkit.inventory.meta.SkullMeta;

/** Created by wixu on 6/22/17. */
public class HeadTexture {

    private static Constructor<?> RESOLVABLE_PROFILE_CONSTRUCTOR;

    static {
        try {
            Class<?> resolvableProfileClass = Class.forName("net.minecraft.world.item.component.ResolvableProfile");
            RESOLVABLE_PROFILE_CONSTRUCTOR =
                    resolvableProfileClass == null ? null : resolvableProfileClass.getConstructor(GameProfile.class);
        } catch (Exception ignored) {
            // old version, no resolvable profile class.
        }
    }

    public static SkullMeta applyToMeta(@NonNull SkullMeta meta, @NonNull String base64) {
        Property property = new Property("textures", base64);
        PropertyMap propertyMap = new PropertyMap(HashMultimap.create());
        propertyMap.put("textures", property);
        GameProfile profile = new GameProfile(UUID.randomUUID(), "", propertyMap);

        if (ServerVersion.getVersion().gte(ServerVersion.v1_18_R0)) {
            setProfileToMeta_gt_1_16(meta, profile);
        } else {
            setProfileToMeta_lte_1_16(meta, profile);
        }
        return meta;
    }

    private static void setProfileToMeta_gt_1_16(@NonNull SkullMeta skullMeta, @NonNull GameProfile gameProfile) {
        Method method;
        try {
            method = skullMeta
                    .getClass()
                    .getDeclaredMethod(
                            "setProfile",
                            RESOLVABLE_PROFILE_CONSTRUCTOR == null
                                    ? GameProfile.class
                                    : RESOLVABLE_PROFILE_CONSTRUCTOR.getDeclaringClass());
            method.setAccessible(true);
            method.invoke(
                    skullMeta,
                    RESOLVABLE_PROFILE_CONSTRUCTOR == null
                            ? gameProfile
                            : RESOLVABLE_PROFILE_CONSTRUCTOR.newInstance(gameProfile));
        } catch (Exception ex) {
            CorePlugin.getPlugin().getLogger().log(Level.WARNING, "Failed to set profile to skull meta (1.18+)", ex);
        }
    }

    private static void setProfileToMeta_lte_1_16(@NonNull SkullMeta skullMeta, @NonNull GameProfile gameProfile) {
        Field field;
        try {
            field = skullMeta.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            field.set(skullMeta, gameProfile);
        } catch (Exception ex) {
            Messenger.warning("Failed to apply skull game profile");
            CorePlugin.getPlugin().getLogger().log(Level.WARNING, "Failed to set profile to skull meta (<=1.16)", ex);
        }
    }

    public static String fromMeta(SkullMeta meta) {
        if (meta == null) {
            throw new IllegalArgumentException("Meta cannot be null!");
        }
        Field profileField = null;
        try {
            profileField = meta.getClass().getDeclaredField("profile");
        } catch (NoSuchFieldException e) {
            CorePlugin.getPlugin().getLogger().log(Level.FINE, "Profile field not found in skull meta", e);
        }
        if (profileField != null) {
            profileField.setAccessible(true);

            try {
                Object object = profileField.get(meta);
                if (object == null) {
                    return null;
                }
                GameProfile profile = (GameProfile) object;
                PropertyMap map = profile.properties();
                Collection<Property> propertyCollection = map.get("textures");
                Property property = propertyCollection.iterator().next();
                if (property != null) {
                    return property.value();
                }
            } catch (IllegalAccessException e) {
                CorePlugin.getPlugin().getLogger().log(Level.WARNING, "Failed to access profile field value", e);
            }
        }
        return null;
    }
}
