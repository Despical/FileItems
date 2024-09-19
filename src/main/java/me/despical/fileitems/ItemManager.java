package me.despical.fileitems;

import io.th0rgal.oraxen.api.OraxenItems;
import me.despical.commons.compat.XMaterial;
import me.despical.commons.configuration.ConfigUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.logging.Level;

import static me.despical.fileitems.ItemOption.*;

/**
 * @author Despical
 * <p>
 * Created at 18.09.2024
 */
public final class ItemManager {

	private Consumer<ItemBuilder> builderEditor;

	private final JavaPlugin plugin;
	private final boolean oraxenEnabled;
	private final Map<String, SpecialItem> items;
	private final Map<String, Function<Object, Object>> customKeys;

	public ItemManager(@NotNull JavaPlugin plugin) {
		this.plugin = plugin;
		this.items = new HashMap<>();
		this.oraxenEnabled = plugin.getServer().getPluginManager().isPluginEnabled("Oraxen");
		this.customKeys = new HashMap<>();
	}

	public SpecialItem getItem(@NotNull String itemName) {
		return this.items.get(itemName);
	}

	public Optional<SpecialItem> findItem(@Nullable String itemName) {
		return itemName == null ? Optional.empty() : Optional.ofNullable(this.items.get(itemName));
	}

	public void addCustomKey(@NotNull String key) {
		this.addCustomKey(key, UnaryOperator.identity());
	}

	public void addCustomKeys(@NotNull String... keys) {
		for (final var key : keys) {
			this.addCustomKey(key);
		}
	}

	public void addCustomKey(@NotNull String key, @NotNull Function<Object, Object> keyMapper) {
		this.customKeys.put(key, keyMapper);
	}

	public void editItemBuilder(Consumer<ItemBuilder> builderConsumer) {
		this.builderEditor = builderConsumer;
	}

	public void registerItems(@NotNull String fileName, @NotNull String path) {
		FileConfiguration config = ConfigUtils.getConfig(plugin, fileName);
		ConfigurationSection section = config.getConfigurationSection(path);

		if (section == null) {
			throw new NoSuchElementException("No such configuration section exists!");
		}

		for (String key : section.getKeys(false)) {
			String materialName = section.getString(MATERIAL.getFormattedPath(key));

			if (materialName == null) {
				plugin.getLogger().log(Level.WARNING, "Material name does not exists for the key ''{0}''!", key);
				continue;
			}

			ItemBuilder itemBuilder = this.createItemBuilder(materialName)
				.name(section.getString(NAME.getFormattedPath(key)))
				.amount(section.getInt(AMOUNT.getFormattedPath(key), 1))
				.durability((short) section.getInt(DURABILITY.getFormattedPath(key)))
				.data((byte) section.getInt(DATA.getFormattedPath(key)))
				.unbreakable(section.getBoolean(UNBREAKABLE.getFormattedPath(key)))
				.glow(section.getBoolean(GLOW.getFormattedPath(key)))
				.hideToolTip(section.getBoolean(HIDE_TOOL_TIPS.getFormattedPath(key)))
				.lore(section.getStringList(LORE.getFormattedPath(key)))
				.flag(section.getStringList(ITEM_FLAGS.getFormattedPath(key))
					.stream()
					.map(ItemFlag::valueOf)
					.toArray(ItemFlag[]::new));

			Optional.ofNullable(this.builderEditor).ifPresent(consumer -> consumer.accept(itemBuilder));

			SpecialItem item = new SpecialItem(itemBuilder.build());

			custom_keys:
			{
				if (CUSTOM_KEYS.isSkipped()) {
					break custom_keys;
				}

				for (final var entry : this.customKeys.entrySet()) {
					String entryKey = entry.getKey();
					Object value = section.get("%s.%s".formatted(key, entryKey));

					item.addCustomKey(entryKey, entry.getValue().apply(value));
				}
			}

			this.items.put(key, item);
		}
	}

	@NotNull
	private ItemBuilder createItemBuilder(String materialName) {
		if (!oraxenEnabled) {
			Material material = XMaterial.matchXMaterial(materialName).orElseThrow().parseMaterial();
			return new ItemBuilder(material);
		}

		final String identifier = ORAXEN_IDENTIFIER.getPath();

		if (!materialName.startsWith(identifier)) {
			throw new IllegalArgumentException("Material name does not start with the Oraxen identifier: '%s'".formatted(materialName));
		}

		materialName = materialName.substring(identifier.length());

		var itemBuilder = OraxenItems.getItemById(materialName);

		if (itemBuilder == null) {
			throw new NullPointerException("We could not find an item called '%s' using the Oraxen API!".formatted(materialName));
		}

		return new ItemBuilder(itemBuilder.build());
	}
}