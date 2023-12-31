package org.wallentines.midnightcore.api.player;

import org.wallentines.midnightcore.api.item.MItemStack;
import org.wallentines.midnightcore.api.module.skin.Skinnable;
import org.wallentines.midnightcore.api.server.MServer;
import org.wallentines.midnightcore.api.text.MComponent;
import org.wallentines.midnightlib.registry.Identifier;

import java.util.UUID;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public interface MPlayer extends Skinnable {

    UUID getUUID();

    String getUsername();

    MComponent getName();

    MServer getServer();

    Location getLocation();

    MItemStack getItemInMainHand();
    MItemStack getItemInOffhand();

    String getLocale();

    boolean isOffline();
    boolean hasPermission(String permission);
    boolean hasPermission(String permission, int permissionLevel);

    void sendMessage(MComponent component);
    void sendActionBar(MComponent component);
    void sendTitle(MComponent component, int fadeIn, int stay, int fadeOut);
    void sendSubtitle(MComponent component, int fadeIn, int stay, int fadeOut);
    void clearTitles();

    void playSound(Identifier soundId, String category, float volume, float pitch);

    void closeContainer();

    void executeCommand(String cmd);
    void sendChatMessage(String message);

    void giveItem(MItemStack item);
    void giveItem(MItemStack item, int slot);

    void teleport(Location newLoc);

    void setGameMode(GameMode gameMode);

    GameMode getGameMode();

    float getHealth();

    void applyResourcePack(String url, String hash, boolean force, MComponent promptMessage, Consumer<ResourcePackStatus> onResponse);

    enum ResourcePackStatus {
        LOADED,
        DECLINED,
        FAILED,
        ACCEPTED
    }

    enum GameMode {
        SURVIVAL,
        CREATIVE,
        ADVENTURE,
        SPECTATOR
    }
}
