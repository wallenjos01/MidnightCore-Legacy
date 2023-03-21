package org.wallentines.midnightcore.common.module.session;

import org.wallentines.mdcfg.ConfigObject;
import org.wallentines.mdcfg.codec.FileWrapper;
import org.wallentines.mdcfg.serializer.ConfigContext;
import org.wallentines.mdcfg.serializer.SerializeResult;
import org.wallentines.midnightcore.api.FileConfig;
import org.wallentines.midnightcore.api.MidnightCoreAPI;
import org.wallentines.midnightcore.api.module.savepoint.Savepoint;
import org.wallentines.midnightcore.api.module.savepoint.SavepointModule;
import org.wallentines.midnightcore.api.module.session.Session;
import org.wallentines.midnightcore.api.module.session.SessionModule;
import org.wallentines.midnightcore.api.player.MPlayer;
import org.wallentines.midnightcore.api.server.MServer;
import org.wallentines.mdcfg.ConfigSection;
import org.wallentines.midnightcore.common.util.FileUtil;
import org.wallentines.midnightlib.event.HandlerList;
import org.wallentines.midnightlib.registry.Identifier;

import java.io.File;
import java.util.*;
import java.util.function.Predicate;

public abstract class AbstractSessionModule implements SessionModule {

    private final HashMap<UUID, Session> sessions = new HashMap<>();
    private final HandlerList<Session> shutdownCallbacks = new HandlerList<>();
    private final HandlerList<Session.SessionPlayerEvent> joinCallbacks = new HandlerList<>();
    private final HandlerList<Session.SessionPlayerEvent> leaveCallbacks = new HandlerList<>();

    private File tempFolder;


    @Override
    public void registerSession(Session session) {

        if(sessions.containsKey(session.getId())) {
            throw new IllegalStateException("Attempt to register session with duplicate ID!");
        }

        sessions.put(session.getId(), session);
        session.joinEvent().register(this, ev -> {

            FileConfig config = FileConfig.findOrCreate(ev.getPlayer().getUUID().toString(), tempFolder);
            SerializeResult<ConfigObject> obj = ev.getSession().getSavepointModule().getSerializer().serialize(ConfigContext.INSTANCE, ev.getSession().getSavepoint(ev.getPlayer()));

            if(!obj.isComplete()) {
                MidnightCoreAPI.getLogger().warn("Failed to save data for player " + ev.getPlayer().getUsername() + "!" + obj.getError());
                return;
            }

            config.setRoot(obj.getOrThrow());
            config.save();
        });
        session.shutdownEvent().register(this, sess -> {

            shutdownEvent().invoke(sess);
            sessions.remove(sess.getId());
        });
        session.joinEvent().register(this, ev -> joinEvent().invoke(ev));
        session.leaveEvent().register(this, ev -> {

            leaveEvent().invoke(ev);

            FileConfig config = FileConfig.findOrCreate(ev.getPlayer().getUUID().toString(), tempFolder);
            if(config.getFile().exists() && !config.getFile().delete()) {
                MidnightCoreAPI.getLogger().warn("Unable to delete session cache for player " + ev.getPlayer().getUsername() + "!");
            }
        });
    }

    @Override
    public Session getSession(UUID id) {
        return sessions.get(id);
    }

    @Override
    public Session getSession(MPlayer player) {

        for(Session s : sessions.values()) {
            if(s.contains(player)) return s;
        }

        return null;
    }

    @Override
    public boolean isInSession(MPlayer player) {

        for(Session s : sessions.values()) {
            if(s.contains(player)) return true;
        }

        return false;
    }

    @Override
    public void shutdownSession(Session session) {

        UUID id = session.getId();

        session.shutdown();
        sessions.remove(id);
    }

    @Override
    public void shutdownAll() {

        List<Session> sess = new ArrayList<>(sessions.values());
        for(Session s : sess) {
            s.shutdown();
        }

        sessions.clear();
    }

    @Override
    public void shutdownAll(Predicate<Session> test) {

        List<Session> sess = new ArrayList<>(sessions.values());
        for(Session s : sess) {
            if(test.test(s)) {
                s.shutdown();
            }
        }
    }

    @Override
    public boolean initialize(ConfigSection section, MServer data) {

        data.tickEvent().register(this, ev -> tickAll());
        tempFolder = FileUtil.tryCreateDirectory(data.getMidnightCore().getDataFolder().toPath().resolve("sessionCache"));

        return tempFolder != null;
    }

    @Override
    public Collection<Session> getSessions() {
        return sessions.values();
    }

    protected void tickAll() {

        for(Session sess : sessions.values()) {
            sess.tick();
        }

    }

    protected void onJoin(MPlayer player) {

        // In the event of a server crash while a session is loaded, player data will not be restored. In such a case,
        // the data will be loaded from disk when they log back in.

        FileWrapper<ConfigObject> config = FileConfig.find(player.getUUID().toString(), tempFolder);
        if(config == null || config.getRoot() == null) return;

        SavepointModule mod = getAPI().getServer().getModule(SavepointModule.class);
        SerializeResult<Savepoint> loaded = mod.getSerializer().deserialize(ConfigContext.INSTANCE, config.getRoot());

        if(config.getFile().exists() && !config.getFile().delete()) {
            MidnightCoreAPI.getLogger().warn("Unable to delete session cache for player " + player.getUsername() + "!");
        }

        if(!loaded.isComplete()) {
            MidnightCoreAPI.getLogger().warn("Failed to restore data for player " + player.getUsername() + "!" + loaded.getError());
            return;
        }

        loaded.getOrThrow().load(player);
    }

    @Override
    public HandlerList<Session> shutdownEvent() {
        return shutdownCallbacks;
    }

    @Override
    public HandlerList<Session.SessionPlayerEvent> joinEvent() {
        return joinCallbacks;
    }

    @Override
    public HandlerList<Session.SessionPlayerEvent> leaveEvent() {
        return leaveCallbacks;
    }

    public static final Identifier ID = new Identifier(MidnightCoreAPI.DEFAULT_NAMESPACE, "session");
}
