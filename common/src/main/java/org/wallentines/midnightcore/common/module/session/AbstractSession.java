package org.wallentines.midnightcore.common.module.session;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wallentines.midnightcore.api.MidnightCoreAPI;
import org.wallentines.midnightcore.api.module.lang.LangModule;
import org.wallentines.midnightcore.api.module.lang.LangProvider;
import org.wallentines.midnightcore.api.module.lang.PlaceholderSupplier;
import org.wallentines.midnightcore.api.module.savepoint.SavepointModule;
import org.wallentines.midnightcore.api.module.session.Session;
import org.wallentines.midnightcore.api.player.MPlayer;
import org.wallentines.midnightcore.api.text.MComponent;
import org.wallentines.midnightcore.common.Constants;
import org.wallentines.midnightlib.event.Event;
import org.wallentines.midnightlib.registry.Identifier;

import java.util.*;
import java.util.function.Function;

public abstract class AbstractSession implements Session {

    protected static final Logger LOGGER = LogManager.getLogger("Session");
    public static final Random RANDOM = new Random();

    private final UUID uid;

    private final SavepointModule spm;
    private final Identifier spId;

    private boolean shutdown = false;

    private final Set<MPlayer> players = new HashSet<>();

    private final List<Runnable> shutdownCallbacks = new ArrayList<>();

    public AbstractSession() {
        this(Constants.DEFAULT_NAMESPACE);
    }

    public AbstractSession(String namespace) {

        this.uid = UUID.randomUUID();

        this.spm = MidnightCoreAPI.getInstance().getModuleManager().getModule(SavepointModule.class);
        this.spId = new Identifier(namespace, uid.toString());
    }

    @Override
    public final UUID getId() {
        return uid;
    }

    @Override
    public final boolean addPlayer(MPlayer player) {

        if(players.contains(player)) return false;
        if(!shouldAddPlayer(player)) return false;

        spm.resetPlayer(player);
        spm.savePlayer(player, spId);

        players.add(player);

        onAddPlayer(player);
        return true;
    }

    @Override
    public void removePlayer(MPlayer player) {

        spm.loadPlayer(player, spId);
        spm.removeSavePoint(player, spId);

        players.remove(player);

        onRemovePlayer(player);

        if (players.isEmpty()) {
            shutdown();
        }

    }

    @Override
    public void addShutdownCallback(Runnable runnable) {
        shutdownCallbacks.add(runnable);
    }

    @Override
    public Collection<MPlayer> getPlayers() {
        return new HashSet<>(players);
    }

    @Override
    public Collection<MPlayer> getPlayers(Function<MPlayer, Boolean> filter) {
        HashSet<MPlayer> out = new HashSet<>();
        for(MPlayer pl : players) {
            if(filter.apply(pl)) out.add(pl);
        }
        return out;
    }

    @Override
    public MPlayer getRandomPlayer() {

        int count = getPlayerCount();
        if(count == 0) throw new IllegalStateException("Attempt to obtain a random player from an empty session!");

        int index = 0;
        int rIndex = RANDOM.nextInt(count);

        for (MPlayer pl : getPlayers()) {
            if (rIndex == index) return pl;
            index++;
        }

        return getPlayers().iterator().next();
    }

    @Override
    public int getPlayerCount() {
        return getPlayers().size();
    }

    @Override
    public boolean contains(MPlayer player) {
        return getPlayers().contains(player);
    }

    @Override
    public void shutdown() {

        if (shutdown) {
            return;
        }
        shutdown = true;

        List<MPlayer> toRemove = new ArrayList<>(players);
        for (MPlayer pl : toRemove) {
            removePlayer(pl);
        }

        for (Runnable run : shutdownCallbacks) {
            try {
                run.run();
            } catch (Exception ex) {
                LOGGER.warn("An error occurred while shutting down a session!");
                ex.printStackTrace();
            }
        }

        onShutdown();
        Event.unregisterAll(this);
    }

    @Override
    public void broadcastMessage(MComponent message) {
        for(MPlayer pl : getPlayers()) {
            pl.sendMessage(message);
        }
    }

    @Override
    public void broadcastMessage(String key, LangProvider provider, Object... data) {

        HashMap<String, MComponent> cache = new HashMap<>();
        for(MPlayer pl : getPlayers()) {
            pl.sendMessage(cache.computeIfAbsent(pl.getLocale(), k -> provider.getMessage(key, pl, data)));
        }
    }

    protected abstract boolean shouldAddPlayer(MPlayer player);
    protected abstract void onAddPlayer(MPlayer player);
    protected abstract void onRemovePlayer(MPlayer player);
    protected abstract void onShutdown();

    public static void registerPlaceholders(LangModule module) {

        if(module == null) return;
        module.registerInlinePlaceholder(Constants.DEFAULT_NAMESPACE + "_session_player_count", PlaceholderSupplier.create(AbstractSession.class, s -> s.getPlayerCount() + "", () -> "0"));
    }

}