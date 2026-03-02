/*
 * File Items - A library for loading items from YAMLs
 * Copyright (C) 2026  Berke Akçen
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package dev.despical.fileitems;

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

    private final String key;
    private final ItemStack itemStack;
    private final Map<String, Object> customKeys;

    SpecialItem(String key, ItemStack itemStack) {
        this.key = key;
        this.itemStack = itemStack;
        this.customKeys = new HashMap<>();
    }

    /**
     * Retrieves a clone of the underlying {@code ItemStack}.
     * <p>
     * Modifying the returned stack will not affect the stored item.
     *
     * @return a clone of the stored {@code ItemStack}
     */
    @NotNull
    public ItemStack getItemStack() {
        return itemStack.clone();
    }

    /**
     * Retrieves the original underlying {@code ItemStack}.
     * <p>
     * <b>Note:</b> Modifying this stack will directly affect the stored item.
     *
     * @return the original {@code ItemStack}
     */
    @NotNull
    public ItemStack getOriginalItemStack() {
        return itemStack;
    }

    /**
     * Creates a new {@code ItemBuilder} initialized with this item's stack.
     *
     * @return a new {@code ItemBuilder} wrapping the item stack
     */
    @NotNull
    public ItemBuilder asItemBuilder() {
        return new ItemBuilder(itemStack);
    }

    /**
     * Gets the unique key identifier for this special item.
     *
     * @return the unique key string
     */
    @NotNull
    public String getKey() {
        return key;
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

    /**
     * Returns an {@code Optional} containing the value mapped to the specified key,
     * or an empty {@code Optional} if no mapping exists.
     *
     * @param key the key whose associated value is to be returned
     * @param <T> the type to cast the value to
     * @return an {@code Optional} containing the value, or empty if not found
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> findCustomKey(@NotNull String key) {
        return Optional.ofNullable((T) customKeys.get(key));
    }

    /**
     * Associates the specified value with the specified key in the custom keys map.
     *
     * @param key   the key with which the specified value is to be associated
     * @param value the value to be associated with the specified key
     */
    void addCustomKey(String key, Object value) {
        customKeys.put(key, value);
    }

    /**
     * Checks if a Bukkit {@code ItemStack} matches this SpecialItem based on
     * material type, display name, and lore.
     *
     * @param item the {@code ItemStack} to compare
     * @return {@code true} if the item matches this SpecialItem's metadata, {@code false} otherwise
     */
    public boolean equals(@Nullable ItemStack item) {
        if (item == null) return false;

        ItemMeta meta = item.getItemMeta();
        ItemMeta itemMeta = itemStack.getItemMeta();

        return item.getType() == itemStack.getType() &&
            Objects.equals(meta.getDisplayName(), itemMeta.getDisplayName()) &&
            Objects.equals(meta.getLore(), itemMeta.getLore());
    }
}
