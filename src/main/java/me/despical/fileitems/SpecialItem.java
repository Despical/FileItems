package me.despical.fileitems;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
		return this.itemStack;
	}

	public <T> T getCustomKey(final @NotNull String key) {
		return (T) this.customKeys.get(key);
	}

	void addCustomKey(String key, Object value) {
		this.customKeys.put(key, value);
	}

	public boolean equals(ItemStack item) {
		final var meta = item.getItemMeta();
		final var itemMeta = item.getItemMeta();

		return item.getType() == itemStack.getType() &&
			Objects.equals(meta.getDisplayName(), itemMeta.getDisplayName()) &&
			Objects.equals(meta.getLore(), itemMeta.getLore());
	}
}