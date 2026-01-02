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

import dev.despical.commons.util.Strings;

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