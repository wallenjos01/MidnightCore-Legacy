package org.wallentines.midnightcore.api.module.session;

import org.wallentines.midnightcore.api.MidnightCoreAPI;
import org.wallentines.midnightcore.api.player.MPlayer;
import org.wallentines.midnightlib.module.Module;

import java.util.Collection;
import java.util.UUID;

public interface SessionModule extends Module<MidnightCoreAPI> {

    void registerSession(Session session);

    Session getSession(UUID id);

    Session getSession(MPlayer player);

    boolean isInSession(MPlayer player);

    void shutdownSession(Session session);

    void shutdownAll();

    Collection<Session> getSessions();

}