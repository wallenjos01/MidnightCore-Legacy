package me.m1dnightninja.midnightcore.api.module.pluginmessage;

import me.m1dnightninja.midnightcore.api.config.ConfigSection;
import me.m1dnightninja.midnightcore.api.player.MPlayer;

public interface IPluginMessageHandler {

    void handle(MPlayer player, ConfigSection configSection);

    /**
     * If true, messages under this channel from clients will be sent to servers, and
     * message under this channel from servers will be sent to clients.
     * Use this is the client and server need to communicate directly, as opposed to
     * the server and proxy.
     *
     * NOTE: If there is no proxy between the client and server, this value will effectively
     * always be true!
     *
     * @return Whether this packet should be visible to players.
     */
    default boolean visibleToPlayers() { return false; }

}