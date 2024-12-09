package me.despical.fileitems;

import io.th0rgal.oraxen.api.OraxenItems;
import me.despical.commons.compat.XMaterial;
import me.despical.commons.configuration.ConfigUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
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
	private final Map<String, SpecialItem> items;
	private final Map<String, Map<String, SpecialItem>> categoriedItems;
	private final Map<String, Function<Object, Object>> customKeys;

	public ItemManager(@NotNull JavaPlugin plugin) {
		this(plugin, null);
	}

	public ItemManager(@NotNull JavaPlugin plugin, @Nullable Consumer<ItemManager> function) {
		this.plugin = plugin;
		this.items = new HashMap<>();
		this.categoriedItems = new HashMap<>();
		this.customKeys = new HashMap<>();

		Optional.ofNullable(function).ifPresent(consumer -> consumer.accept(this));
	}

	public SpecialItem getItem(@NotNull String itemName) {
		return this.items.get(itemName);
	}

	public Collection<SpecialItem> getItems() {
		return this.items.values();
	}

	public SpecialItem getItemFromCategory(@NotNull String categoryName, @NotNull String itemName) {
		return this.categoriedItems.get(categoryName).get(itemName);
	}

	public Collection<SpecialItem> getItemsFromCategory(@NotNull String categoryName) {
		return this.categoriedItems.get(categoryName).values();
	}

	public Optional<SpecialItem> findItem(@Nullable String itemName) {
		return itemName == null ? Optional.empty() : Optional.ofNullable(this.items.get(itemName));
	}

	public void addCustomKey(@NotNull String key) {
		this.addCustomKey(key, UnaryOperator.identity());
	}

	public void addCustomKeys(@NotNull String... keys) {
		for (String key : keys) {
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
		registerItems(path, ConfigUtils.getConfig(plugin, fileName));
	}

	public void registerItemsFromResources(@NotNull String fileName, @NotNull String path) {
		registerItems(path, ConfigUtils.getConfigFromResources(plugin, fileName));
	}

	public void registerItems(@NotNull String categoryName, @NotNull String path, FileConfiguration config) {
		ConfigurationSection section = config.getConfigurationSection(path);

		if (section == null) {
			throw new NullPointerException("No such configuration section exists!");
		}

		this.categoriedItems.put(categoryName, getSectionItems(section));
	}

	private void registerItems(@NotNull String path, FileConfiguration config) {
		ConfigurationSection section = config.getConfigurationSection(path);

		if (section == null) {
			throw new NullPointerException("No such configuration section exists!");
		}

		this.items.putAll(getSectionItems(section));
	}

	private Map<String, SpecialItem> getSectionItems(@NotNull ConfigurationSection section) {
		Map<String, SpecialItem> items = new HashMap<>();

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
				.hideTooltip(section.getBoolean(HIDE_TOOLTIP.getFormattedPath(key)))
				.lore(section.getStringList(LORE.getFormattedPath(key)))
				.flag(section.getStringList(ITEM_FLAGS.getFormattedPath(key))
					.stream()
					.map(ItemFlag::valueOf)
					.toArray(ItemFlag[]::new))
				.consume(builderEditor);

			List<String> enchantments = section.getStringList(ENCHANTS.getFormattedPath(key));
			for (String enchant : enchantments) {
				String[] parts = enchant.split(" ");

				if (parts.length != 2) {
					throw new IllegalArgumentException("Invalid enchantment format. Expected 'name:level'.");
				}

				String name = parts[0];
				int level = Integer.parseInt(parts[1]);

				itemBuilder.enchantment(Enchantment.getByName(name.toUpperCase(Locale.ROOT)), level);
			}

			SpecialItem item = new SpecialItem(itemBuilder.build());

			custom_keys: {
				if (CUSTOM_KEYS.isSkipped()) {
					break custom_keys;
				}

				for (var entry : customKeys.entrySet()) {
					String entryKey = entry.getKey();
					Object value = section.get("%s.%s".formatted(key, entryKey));

					item.addCustomKey(entryKey, entry.getValue().apply(value));
				}
			}

			items.put(key, item);
		}

		return items;
	}

	@NotNull
	private ItemBuilder createItemBuilder(String materialName) {
		boolean oraxenEnabled = plugin.getServer().getPluginManager().isPluginEnabled("Oraxen");

		if (!oraxenEnabled) {
			ItemStack itemStack = XMaterial.matchXMaterial(materialName).orElseThrow().parseItem();
			return new ItemBuilder(itemStack);
		}

		String identifier = ORAXEN.getPath();

		if (!materialName.startsWith(identifier)) {
			ItemStack itemStack = XMaterial.matchXMaterial(materialName).orElseThrow().parseItem();
			return new ItemBuilder(itemStack);
		}

		materialName = materialName.substring(identifier.length());

		var itemBuilder = OraxenItems.getItemById(materialName);

		if (itemBuilder == null) {
			throw new NullPointerException("We could not find an item called '%s' using the Oraxen API!".formatted(materialName));
		}

		return new ItemBuilder(itemBuilder.build());
	}
}