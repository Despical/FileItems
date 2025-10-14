package me.despical.fileitems;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Despical
 * <p>
 * Created at 17.09.2024
 */
public final class SpecialItem {

    private final ItemStack itemStack;
    private final Map<String, Object> customKeys;

    SpecialItem(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.customKeys = new HashMap<>();
    }

    @NotNull
    public ItemStack getItemStack() {
        return itemStack.clone();
    }

    @NotNull
    public ItemStack getOriginalItemStack() {
        return itemStack;
    }

    @NotNull
    public ItemBuilder asItemBuilder() {
        return new ItemBuilder(itemStack);
    }

    /**
     * Stores the {@code ItemStack} of this {@code SpecialItem} instance
     * at the given {@code slot} index of the given {@code player}'s inventory.
     *
     * @param player the player who will receive the item
     * @param slot   the slot index to store the item stack
     */
    public void giveTo(Player player, int slot) {
        player.getInventory().setItem(slot, itemStack);
    }

    /**
     * Retrieves the slot index associated with the given custom key and stores
     * the {@code ItemStack} of this {@code SpecialItem} instance in that slot
     * of the specified {@code player}'s inventory.
     *
     * @param player the player who will receive the item
     * @param key    the custom key to retrieve the slot index
     */
    public void giveTo(Player player, String key) {
        int slot = getCustomKey(key);
        giveTo(player, slot);
    }

    /**
     * Returns the value to which the specified key is mapped,
     * or {@code null} if the custom keys map contains no mapping
     * for the key.
     *
     * @param key the key whose associated value is to be returned
     * @param <T> the class of the objects in the custom keys Map.
     * @return the value to which the specified key is mapped, or
     * {@code null} if this map contains no mapping for the key
     */
    @SuppressWarnings("unchecked")
    public <T> T getCustomKey(@NotNull String key) {
        return (T) customKeys.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> findCustomKey(@NotNull String key) {
        return Optional.ofNullable((T) customKeys.get(key));
    }

    void addCustomKey(String key, Object value) {
        customKeys.put(key, value);
    }

    public boolean equals(@Nullable ItemStack item) {
        if (item == null) return false;

        ItemMeta meta = item.getItemMeta();
        ItemMeta itemMeta = itemStack.getItemMeta();

        return item.getType() == itemStack.getType() &&
            Objects.equals(meta.getDisplayName(), itemMeta.getDisplayName()) &&
            Objects.equals(meta.getLore(), itemMeta.getLore());
    }
}