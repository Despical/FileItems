package me.despical.fileitems;

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
	public ItemBuilder asItemBuilder() {
		return new ItemBuilder(itemStack);
	}

	/**
	 * Returns the value to which the specified key is mapped,
	 * or {@code null} if the custom keys map contains no mapping
	 * for the key.
	 *
	 * @param key the key whose associated value is to be returned
	 * @return the value to which the specified key is mapped, or
	 *         {@code null} if this map contains no mapping for the key
	 * @param <T> the class of the objects in the custom keys Map.
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