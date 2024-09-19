package me.despical.fileitems;

import me.despical.commons.util.Strings;

import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * @author Despical
 * <p>
 * Created at 18.09.2024
 */
public enum ItemOption {

	AMOUNT("amount", true),
	CUSTOM_KEYS(null, true),
	DATA("data", true),
	DURABILITY("durability", true),
	GLOW("glow", true),
	HIDE_TOOL_TIPS("hideToolTips", true),
	ITEM_FLAGS("itemFlags", true),
	LORE("lore", false),
	MATERIAL("material", false),
	NAME("name", false),
	ORAXEN_IDENTIFIER("oraxen:", true),
	UNBREAKABLE("unbreakable", true);

	private static UnaryOperator<String> colorFormatter = Strings::format;

	private boolean skip;
	private String path;

	ItemOption(String path, boolean skip) {
		this.path = path;
		this.skip = skip;
	}

	public static void enableOptions(ItemOption... options) {
		Stream.of(options).forEach(option -> option.skip = false);
	}

	public static void disableOptions(ItemOption... options) {
		Stream.of(options).forEach(option -> option.skip = true);
	}

	static String formatColors(String string) {
		return colorFormatter.apply(string);
	}

	public static void setColorFormatter(UnaryOperator<String> colorFormatter) {
		ItemOption.colorFormatter = colorFormatter;
	}

	String getFormattedPath(String key) {
		return "%s.%s".formatted(key, this.path);
	}

	boolean isSkipped() {
		return this.skip;
	}

	public void setPath(String path) {
		this.path = path;
	}
}