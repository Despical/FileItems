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

import com.google.common.collect.Multimap;
import dev.despical.commons.XEnchantment;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static dev.despical.fileitems.ItemOption.*;

/**
 * @author Despical
 * <p>
 * Created at 17.09.2024
 */
public final class ItemBuilder {

    private final ItemStack itemStack;
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final boolean SUPPORTS_CUSTOM_MODEL_DATA = hasMethod(ItemMeta.class, "setCustomModelData", Integer.class);
    private static final boolean SUPPORTS_UNBREAKABLE = hasMethod(ItemMeta.class, "setUnbreakable", boolean.class);
    private static final boolean SUPPORTS_DEFAULT_ATTRIBUTE_MODIFIERS = hasMethod(Material.class, "getDefaultAttributeModifiers", EquipmentSlot.class);

    ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack.clone();
    }

    private Component parseMiniMessage(String text) {
        if (text == null || text.isEmpty()) return Component.empty();
        return MINI_MESSAGE.deserialize("<italic:false>" + text);
    }

    public ItemBuilder name(String displayName) {
        if (NAME.isSkipped()) {
            return this;
        }

        return this.edit(itemMeta -> itemMeta.displayName(parseMiniMessage(displayName)));
    }

    public ItemBuilder lore(Collection<String> lore) {
        if (LORE.isSkipped()) {
            return this;
        }

        return this.edit(itemMeta -> itemMeta.lore(lore.stream()
            .map(this::parseMiniMessage)
            .collect(Collectors.toList())));
    }

    public ItemBuilder customModelData(int data) {
        if (CUSTOM_MODEL_DATA.isSkipped()) {
            return this;
        }

        if (SUPPORTS_CUSTOM_MODEL_DATA) {
            edit(meta -> meta.setCustomModelData(data));
        }

        return this;
    }

    public ItemBuilder data(byte data) {
        if (DATA.isSkipped()) {
            return this;
        }

        this.itemStack.getData().setData(data);
        return this;
    }

    public ItemBuilder amount(int amount) {
        if (AMOUNT.isSkipped()) return this;
        this.itemStack.setAmount(amount);
        return this;
    }

    public ItemBuilder enchantment(Enchantment enchantment, int level) {
        this.itemStack.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public ItemBuilder flag(ItemFlag... itemFlags) {
        if (ITEM_FLAGS.isSkipped()) return this;
        return this.edit(itemMeta -> itemMeta.addItemFlags(itemFlags));
    }

    public ItemBuilder durability(short durability) {
        if (DURABILITY.isSkipped()) return this;
        itemStack.setDurability(durability);
        return this;
    }

    public ItemBuilder unbreakable(boolean unbreakable) {
        if (UNBREAKABLE.isSkipped()) return this;

        return this.edit(itemMeta -> {
            if (SUPPORTS_UNBREAKABLE) {
                itemMeta.setUnbreakable(unbreakable);
            } else {
                try {
                    Method instanceMethod = itemMeta.getClass().getMethod("spigot");
                    instanceMethod.setAccessible(true);

                    Object instance = instanceMethod.invoke(itemMeta);
                    Method unbreakableMethod = instance.getClass().getMethod("setUnbreakable", boolean.class);
                    unbreakableMethod.setAccessible(true);
                    unbreakableMethod.invoke(instance, unbreakable);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });
    }

    public ItemBuilder hideTooltip(boolean hideToolTip) {
        if (HIDE_TOOLTIP.isSkipped()) return this;

        return hideToolTip ? this.edit(itemMeta -> {
            if (SUPPORTS_DEFAULT_ATTRIBUTE_MODIFIERS) {
                try {
                    Method getDefaultAttributeModifiers = Material.class.getMethod("getDefaultAttributeModifiers", EquipmentSlot.class);
                    getDefaultAttributeModifiers.setAccessible(true);

                    Multimap<Attribute, AttributeModifier> defaultAttributes = (Multimap<Attribute, AttributeModifier>) getDefaultAttributeModifiers.invoke(itemStack.getType(), EquipmentSlot.HAND);
                    itemMeta.setAttributeModifiers(defaultAttributes);
                } catch (Throwable exception) {
                    exception.printStackTrace();
                }
            }

            itemMeta.addItemFlags(ItemFlag.values());
        }) : this;
    }

    public ItemBuilder glow(boolean glow) {
        if (GLOW.isSkipped()) {
            return this;
        }

        if (glow) {
            return this.enchantment(XEnchantment.INFINITY.get(), 1).flag(ItemFlag.HIDE_ENCHANTS);
        }
        
        itemStack.removeEnchantment(XEnchantment.INFINITY.get());
        return this;
    }

    public ItemStack build() {
        return this.itemStack;
    }

    private ItemBuilder edit(Consumer<ItemMeta> metaConsumer) {
        final var itemMeta = this.itemStack.getItemMeta();
        
        if (itemMeta != null) {
            metaConsumer.accept(itemMeta);
            this.itemStack.setItemMeta(itemMeta);
        }

        return this;
    }

    ItemBuilder consume(Consumer<ItemBuilder> consumer) {
        if (consumer != null) consumer.accept(this);
        return this;
    }

    private static boolean hasMethod(Class<?> type, String methodName, Class<?>... parameterTypes) {
        try {
            type.getMethod(methodName, parameterTypes);
            return true;
        } catch (NoSuchMethodException exception) {
            return false;
        }
    }
}
