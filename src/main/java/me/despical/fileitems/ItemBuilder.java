package me.despical.fileitems;

import com.google.common.collect.Multimap;
import me.despical.commons.XEnchantment;
import me.despical.commons.reflection.XReflection;
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

import static me.despical.fileitems.ItemOption.*;

/**
 * @author Despical
 * <p>
 * Created at 17.09.2024
 */
public final class ItemBuilder {

	private final ItemStack itemStack;

	ItemBuilder(ItemStack itemStack) {
		this.itemStack = itemStack;
	}

	public ItemBuilder name(String displayName) {
		if (NAME.isSkipped()) {
			return this;
		}

		return this.edit(itemMeta -> itemMeta.setDisplayName(ItemOption.formatColors(displayName)));
	}

	public ItemBuilder lore(Collection<String> lore) {
		if (LORE.isSkipped()) {
			return this;
		}

		return this.edit(itemMeta -> itemMeta.setLore(lore
				.stream()
				.map(ItemOption::formatColors)
				.toList()));
	}

	public ItemBuilder data(byte data) {
		if (DATA.isSkipped()) {
			return this;
		}

		this.itemStack.getData().setData(data);
		return this;
	}

	public ItemBuilder amount(int amount) {
		if (AMOUNT.isSkipped()) {
			return this;
		}

		this.itemStack.setAmount(amount);
		return this;
	}

	public ItemBuilder enchantment(Enchantment enchantment, int level) {
		this.itemStack.addUnsafeEnchantment(enchantment, level);
		return this;
	}

	public ItemBuilder flag(ItemFlag... itemFlags) {
		if (ITEM_FLAGS.isSkipped()) {
			return this;
		}

		return this.edit(itemMeta -> itemMeta.addItemFlags(itemFlags));
	}

	public ItemBuilder durability(short durability) {
		if (DURABILITY.isSkipped()) {
			return this;
		}

		itemStack.setDurability(durability);
		return this;
	}

	public ItemBuilder unbreakable(boolean unbreakable) {
		if (UNBREAKABLE.isSkipped()) {
			return this;
		}

		return this.edit(itemMeta -> {
			if (XReflection.supports(9)) {
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
		if (HIDE_TOOLTIP.isSkipped()) {
			return this;
		}

		return hideToolTip ? this.edit(itemMeta -> {
			if (XReflection.supports(20, 5)) {
				try {
					Method getDefaultAttributeModifiers = Material.class.getMethod("getDefaultAttributeModifiers", EquipmentSlot.class);
					getDefaultAttributeModifiers.setAccessible(true);

					Multimap<Attribute, AttributeModifier> defaultAttributes = (Multimap<Attribute, AttributeModifier>) getDefaultAttributeModifiers.invoke(itemStack.getType(), EquipmentSlot.HAND);
					itemMeta.setAttributeModifiers(defaultAttributes);
				} catch (Throwable ignored) {
					ignored.printStackTrace();
				}
			}

			itemMeta.addItemFlags(ItemFlag.values());
		}) : this;
	}

	public ItemBuilder glow(boolean glow) {
		if (GLOW.isSkipped()) {
			return this;
		}

		return glow ? this.enchantment(XEnchantment.INFINITY.getEnchant(), 1).flag(ItemFlag.HIDE_ENCHANTS) : this;
	}

	public ItemStack build() {
		return this.itemStack;
	}

	private ItemBuilder edit(Consumer<ItemMeta> metaConsumer) {
		final var itemMeta = this.itemStack.getItemMeta();
		metaConsumer.accept(itemMeta);

		this.itemStack.setItemMeta(itemMeta);
		return this;
	}

	ItemBuilder consume(Consumer<ItemBuilder> consumer) {
		if (consumer != null) {
			consumer.accept(this);
		}

		return this;
	}
}