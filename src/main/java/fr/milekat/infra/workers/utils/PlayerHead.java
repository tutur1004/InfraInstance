package fr.milekat.infra.workers.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import fr.milekat.infra.Main;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("SpellCheckingInspection")
public class PlayerHead {
    public static final String Simplistic_Steve = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90" +
            "ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmI4ZWVhN2E3ZDMxZWEwOTI0NWQ3YmQ1NDNhOTMyZTgyYj" +
            "U4ZWY1OTU4ZGRhMTUyZmRiMzUzMTIzODQzNzA5MSJ9fX0=";
    public static final String Arrow_Left = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dX" +
            "Jlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjc2MjMwYTBhYzUyYWYxMWU0YmM4NDAwOWM2ODkwYTQwMjk0NzJm" +
            "Mzk0N2I0ZjQ2NWI1YjU3MjI4ODFhYWNjNyJ9fX0=";
    public static final String Arrow_Right = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0d" +
            "XJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGJmOGI2Mjc3Y2QzNjI2NjI4M2NiNWE5ZTY5NDM5NTNjNzgzZTZ" +
            "mZjdkNmEyZDU5ZDE1YWQwNjk3ZTkxZDQzYyJ9fX0=";
    public static final String Lime_Green = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dX" +
            "Jlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTc2OTVmOTZkZGE2MjZmYWFhMDEwZjRhNWYyOGE1M2NkNjZmNzdk" +
            "ZTBjYzI4MGU3YzU4MjVhZDY1ZWVkYzcyZSJ9fX0=";
    public static final String Gold = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5t" +
            "aW5lY3JhZnQubmV0L3RleHR1cmUvMTQzYzc5Y2Q5YzJkMzE4N2VhMDMyNDVmZTIxMjhlMGQyYWJiZTc5NDUyMT" +
            "RiYzU4MzRkZmE0MDNjMTM0ZTI3In19fQ==";
    public static final String Rose_Red = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJl" +
            "cy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2M0ZDdhM2JjM2RlODMzZDMwMzJlODVhMGJmNmYyYmVmNzY4Nzg2Mm" +
            "IzYzZiYzQwY2U3MzEwNjRmNjE1ZGQ5ZCJ9fX0=";

    public static @NotNull ItemStack getPlayerSkull(String playerName, String displayName, List<String> lore){
        ItemStack itemStack = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();

        assert skullMeta != null;
        skullMeta.setOwner(playerName);
        skullMeta.setDisplayName(displayName);
        skullMeta.setLore(lore);
        itemStack.setItemMeta(skullMeta);

        return itemStack;
    }

    public static @NotNull ItemStack getTextureSkull(String b64Texture, String displayName){
        return getTextureSkull(b64Texture, displayName, new ArrayList<>());
    }

    public static @NotNull ItemStack getTextureSkull(String b64Texture, String displayName, List<String> lore){
        ItemStack itemStack = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();

        assert skullMeta != null;

        setSkinViaBase64(skullMeta, b64Texture);
        skullMeta.setDisplayName(displayName);
        skullMeta.setLore(lore);

        itemStack.setItemMeta(skullMeta);

        return itemStack;
    }

    /**
     * A method used to set the skin of a player skull via a base64 encoded string
     *
     * @param meta the skull meta to modify
     * @param base64 the base64 encoded string
     */
    private static void setSkinViaBase64(SkullMeta meta, String base64) {
        Field profileField;
        try {
            GameProfile profile = new GameProfile(UUID.randomUUID(), "skull-texture");
            profile.getProperties().put("textures", new Property("textures", base64));

            profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);
        } catch (IllegalAccessException | NoSuchFieldException exception) {
            Main.getOwnLogger().severe("There was a severe internal reflection " +
                    "error when attempting to set the skin of a player skull via base64!");
            exception.printStackTrace();
        }
    }
}
