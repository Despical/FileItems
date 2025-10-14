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
    CUSTOM_KEYS(null, false),
    DATA("data", true),
    DURABILITY("durability", true),
    ENCHANTS("enchants", true),
    GLOW("glow", true),
    HIDE_TOOLTIP("hideTooltip", false),
    ITEM_FLAGS("itemFlags", false),
    LORE("lore", false),
    MATERIAL("material", false),
    NAME("name", false),
    ORAXEN("oraxen:", false),
    UNBREAKABLE("unbreakable", false);

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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}