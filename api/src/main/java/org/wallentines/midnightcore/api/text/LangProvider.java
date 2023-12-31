package org.wallentines.midnightcore.api.text;

import org.wallentines.mdcfg.ConfigObject;
import org.wallentines.mdcfg.ConfigSection;
import org.wallentines.mdcfg.codec.FileWrapper;
import org.wallentines.midnightcore.api.FileConfig;
import org.wallentines.midnightcore.api.MidnightCoreAPI;
import org.wallentines.midnightcore.api.player.MPlayer;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;

@SuppressWarnings("unused")
public class LangProvider {

    protected final File folder;
    protected final LangRegistry defaults;
    protected final String serverLocale;
    protected final HashMap<String, LangRegistry> registries = new HashMap<>();

    public LangProvider(Path folder, ConfigSection defaults) {
        this(folder.toFile(), LangRegistry.fromConfigSection(defaults), getServerLocale());
    }
    public LangProvider(Path folder, ConfigSection defaults, String serverLocale) {
        this(folder.toFile(), LangRegistry.fromConfigSection(defaults), serverLocale);
    }
    public LangProvider(File folder, LangRegistry defaults) {
        this(folder, defaults, getServerLocale());
    }
    public LangProvider(File folder, LangRegistry defaults, String serverLocale) {

        if(!folder.isDirectory()) throw new IllegalArgumentException("Attempt to create a provider with non-folder " + folder.getPath() + "!");

        this.folder = folder;
        this.defaults = defaults;
        this.serverLocale = serverLocale;

        FileConfig def = FileConfig.findOrCreate(serverLocale, folder, new ConfigSection());
        LangRegistry reg = LangRegistry.fromConfigSection(def.getRoot().asSection());
        reg.fill(defaults);
        def.setRoot(reg.save());
        def.save();

        registries.put(serverLocale, reg);
    }

    private LangRegistry getEntries(String key) {
        return registries.computeIfAbsent(key, lang -> {
            FileWrapper<ConfigObject> conf = FileConfig.find(lang, folder);
            if(conf != null && conf.getRoot().isSection()) {
                LangRegistry reg = LangRegistry.fromConfigSection(conf.getRoot().asSection());
                return registries.put(lang, reg);
            }
            return registries.get(serverLocale);
        });
    }

    public MComponent getMessage(String key, String locale, Object... args) {

        String message = getRawMessage(key, locale);
        return PlaceholderManager.INSTANCE.parseText(message, args);
    }

    public String getRawMessage(String key, String locale) {

        if(locale == null) locale = serverLocale;
        return getEntries(locale).getMessage(key, () -> getEntries(serverLocale).getMessage(key, () -> key));
    }

    public boolean hasKey(String key) {
        return hasKey(key, serverLocale);
    }

    public boolean hasKey(String key, String locale) {
        return getEntries(locale).hasKey(key);
    }

    public MComponent getMessage(String key, MPlayer player, Object... args) {

        String locale = player == null ? serverLocale : player.getLocale();
        return getMessage(key, locale, args);
    }

    public String getRawMessage(String key, MPlayer player) {

        String locale = player == null ? serverLocale : player.getLocale();
        return getRawMessage(key, locale);
    }

    public void loadEntries(String locale, LangRegistry reg) {
        registries.put(locale, reg);

        FileConfig conf = FileConfig.findOrCreate(locale, folder, new ConfigSection());
        conf.getRoot().asSection().fill(reg.save());
        conf.save();
    }

    public String getDefaultLocale() {
        return serverLocale;
    }

    public File getFolder() {
        return folder;
    }

    public void reload() {

        registries.clear();

        FileWrapper<ConfigObject> def = FileConfig.findOrCreate(serverLocale, folder, new ConfigSection());

        LangRegistry reg = LangRegistry.fromConfigSection(def.getRoot().asSection());
        reg.fill(defaults);

        def.setRoot(reg.save());
        def.save();

        registries.put(serverLocale, reg);
    }

    public static String getServerLocale() {
        return MidnightCoreAPI.getInstance() != null ? MidnightCoreAPI.getInstance().getServerLocale() : "en_us";
    }

    public static void registerPlaceholders(PlaceholderManager manager) {

        manager.getPlaceholders().register("lang", ctx -> {

            String param = ctx.getParameter();
            LangProvider prov = ctx.getArgument(LangProvider.class);
            MPlayer player = ctx.getArgument(MPlayer.class);

            if(prov != null) {
                String locale = player == null ? prov.serverLocale : player.getLocale();
                return prov.getMessage(param, locale, ctx.getArgs());
            }

            return new MTextComponent(param);
        });

    }

}
