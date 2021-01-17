package me.m1dnightninja.midnightcore.common.module;

import me.m1dnightninja.midnightcore.api.lang.AbstractLangProvider;
import me.m1dnightninja.midnightcore.api.module.ILangModule;

import java.util.HashMap;

public abstract class AbstractLangModule<T> implements ILangModule<T> {

    protected final HashMap<String, AbstractLangProvider> providers = new HashMap<>();

    protected final HashMap<String, PlaceholderSupplier<T>> rawPlaceholders = new HashMap<>();
    protected final HashMap<String, PlaceholderSupplier<String>> stringPlaceholders = new HashMap<>();

    @Override
    public String getId() {
        return "lang";
    }

    @Override
    public void registerStringPlaceholder(String key, PlaceholderSupplier<String> supplier) {
        if(!stringPlaceholders.containsKey(key)) {
            stringPlaceholders.put(key, supplier);
        }
    }

    @Override
    public void registerRawPlaceholder(String key, PlaceholderSupplier<T> supplier) {
        if(!rawPlaceholders.containsKey(key)) {
            rawPlaceholders.put(key, supplier);
        }
    }

    @Override
    public String getStringPlaceholderValue(String key, Object... args) {
        return stringPlaceholders.get(key).get(args);
    }

    @Override
    public T getRawPlaceholderValue(String key, Object... args) {
        return rawPlaceholders.get(key).get(args);
    }
}
