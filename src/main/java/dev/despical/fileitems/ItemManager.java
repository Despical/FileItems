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

import dev.despical.commons.XEnchantment;
import dev.despical.commons.XMaterial;
import dev.despical.commons.configuration.ConfigUtils;
import io.th0rgal.oraxen.api.OraxenItems;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;

import static dev.despical.fileitems.ItemOption.*;

/**
 * @author Despical
 * <p>
 * Created at 18.09.2024
 */
public final class ItemManager {

    private Consumer<ItemBuilder> builderConsumer;

    private final JavaPlugin plugin;
    private final Map<String, SpecialItem> items;
    private final Map<String, Map<String, SpecialItem>> categorizedItems;

    public ItemManager(@NotNull JavaPlugin plugin) {
        this(plugin, manager -> {});
    }

    public ItemManager(@NotNull JavaPlugin plugin, @NotNull Consumer<ItemManager> function) {
        this.plugin = plugin;
        this.items = new HashMap<>();
        this.categorizedItems = new HashMap<>();

        function.accept(this);
    }

    public SpecialItem getItem(@NotNull String itemName) {
        return items.get(itemName);
    }

    public Collection<SpecialItem> getItems() {
        return items.values();
    }

    public SpecialItem getItemFromCategory(@NotNull String categoryName, @NotNull String itemName) {
        return categorizedItems.get(categoryName).get(itemName);
    }

    public List<SpecialItem> getItemsFromCategory(@NotNull String categoryName) {
        return List.copyOf(categorizedItems.get(categoryName).values());
    }

    public Optional<SpecialItem> findItem(@Nullable String itemName) {
        return itemName == null ? Optional.empty() : Optional.ofNullable(items.get(itemName));
    }

    public void editItemBuilder(Consumer<ItemBuilder> builderConsumer) {
        this.builderConsumer = builderConsumer;
    }

    public void registerItems(@NotNull String fileName, @NotNull String path) {
        registerItems(path, ConfigUtils.getConfig(plugin, fileName));
    }

    public void registerItemsFromResources(@NotNull String fileName, @NotNull String path) {
        registerItems(path, ConfigUtils.getConfigFromResources(plugin, fileName));
    }

    public void registerItems(@NotNull String categoryName, @NotNull String path, String fileName) {
        registerItems(categoryName, path, ConfigUtils.getConfig(plugin, fileName));
    }

    public void registerItems(@NotNull String categoryName, @NotNull String path, FileConfiguration config) {
        ConfigurationSection section = config.getConfigurationSection(path);

        if (section == null) {
            throw new NullPointerException("No such configuration section exists!");
        }

        categorizedItems.put(categoryName, getSectionItems(section));
    }

    public void registerItemsFromResources(@NotNull String categoryName, @NotNull String path, @NotNull String fileName) {
        registerItems(categoryName, path, ConfigUtils.getConfigFromResources(plugin, fileName));
    }

    private void registerItems(@NotNull String path, FileConfiguration config) {
        ConfigurationSection section = config.getConfigurationSection(path);

        if (section == null) {
            throw new NullPointerException("No such configuration section exists!");
        }

        items.putAll(getSectionItems(section));
    }

    private Map<String, SpecialItem> getSectionItems(@NotNull ConfigurationSection section) {
        Map<String, SpecialItem> items = new LinkedHashMap<>();

        for (String key : section.getKeys(false)) {
            String materialName = section.getString(MATERIAL.getFormattedPath(key));

            if (materialName == null) {
                plugin.getLogger().log(Level.WARNING, "Special item ''{0}'' does not define a 'material' field and was skipped.", key);
                continue;
            }

            ItemBuilder itemBuilder = this.createItemBuilder(materialName)
                .name(section.getString(NAME.getFormattedPath(key)))
                .amount(section.getInt(AMOUNT.getFormattedPath(key), 1))
                .durability((short) section.getInt(DURABILITY.getFormattedPath(key)))
                .data((byte) section.getInt(DATA.getFormattedPath(key)))
                .unbreakable(section.getBoolean(UNBREAKABLE.getFormattedPath(key)))
                .customModelData(section.getInt(CUSTOM_MODEL_DATA.getFormattedPath(key)))
                .glow(section.getBoolean(GLOW.getFormattedPath(key)))
                .hideTooltip(section.getBoolean(HIDE_TOOLTIP.getFormattedPath(key)))
                .lore(section.getStringList(LORE.getFormattedPath(key)))
                .flag(section.getStringList(ITEM_FLAGS.getFormattedPath(key))
                    .stream()
                    .map(ItemFlag::valueOf)
                    .toArray(ItemFlag[]::new))
                .consume(builderConsumer);

            List<String> enchantments = section.getStringList(ENCHANTS.getFormattedPath(key));
            for (String enchant : enchantments) {
                String[] parts = enchant.split(" ");

                if (parts.length != 2) {
                    throw new IllegalArgumentException("Invalid enchantment format inside " + key + ". Expected 'name level'.");
                }

                String name = parts[0];
                int level = Integer.parseInt(parts[1]);

                XEnchantment.of(name.toUpperCase(Locale.ROOT)).ifPresent(xEnch ->
                    itemBuilder.enchantment(xEnch.get(), level)
                );
            }

            SpecialItem item = new SpecialItem(key, itemBuilder.build());

            if (section.isConfigurationSection(key)) {
                ConfigurationSection itemSection = section.getConfigurationSection(key);

                for (String currentKey : itemSection.getKeys(false)) {
                    if (itemSection.isConfigurationSection(currentKey)) {
                        flattenAndAddCustomKeys(currentKey, itemSection.getConfigurationSection(currentKey), item);
                    } else {
                        item.addCustomKey(currentKey, itemSection.get(currentKey));
                    }
                }
            }

            items.put(key, item);
        }

        return items;
    }

    private void flattenAndAddCustomKeys(String parentPath, ConfigurationSection section, SpecialItem item) {
        for (String key : section.getKeys(false)) {
            String fullPath = parentPath + "." + key;

            if (section.isConfigurationSection(key)) {
                flattenAndAddCustomKeys(fullPath, section.getConfigurationSection(key), item);
            } else {
                item.addCustomKey(fullPath, section.get(key));
            }
        }
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
