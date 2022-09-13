package org.wallentines.midnightcore.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wallentines.midnightcore.api.item.InventoryGUI;
import org.wallentines.midnightcore.api.item.MItemStack;
import org.wallentines.midnightcore.api.player.MPlayer;
import org.wallentines.midnightcore.api.player.PlayerManager;
import org.wallentines.midnightcore.api.text.CustomScoreboard;
import org.wallentines.midnightcore.api.text.MComponent;
import org.wallentines.midnightlib.Version;
import org.wallentines.midnightlib.config.ConfigSection;
import org.wallentines.midnightlib.module.ModuleManager;
import org.wallentines.midnightlib.registry.Identifier;
import org.wallentines.midnightlib.registry.Registry;
import org.wallentines.midnightlib.requirement.RequirementType;

import java.io.File;
import java.util.Random;


public abstract class MidnightCoreAPI {

    private static final Logger LOGGER = LogManager.getLogger("MidnightCore");
    private static MidnightCoreAPI INSTANCE;

    protected MidnightCoreAPI() {
        if(INSTANCE == null) INSTANCE = this;
    }

    /**
     * Returns the main config
     *
     * @return The main configuration
     */
    public abstract ConfigSection getConfig();

    /**
     * Gets the plugin/mod's data folder, where all configuration is stored.
     * Typically, "plugins/MidnightCore" on Spigot, and "config/MidnightCore" on Fabric
     *
     * @return The data folder as a File object
     */
    public abstract File getDataFolder();

    /**
     * Returns the version of Minecraft the server is running
     *
     * @return The version
     */
    public abstract Version getGameVersion();

    /**
     * Returns the main module manager
     *
     * @return The module manager
     */
    public abstract ModuleManager<MidnightCoreAPI> getModuleManager();

    /**
     * Returns the requirement type registry, so additional requirement types can be added
     *
     * @return The requirement type registry
     */
    public abstract Registry<RequirementType<MPlayer>> getRequirementRegistry();

    /**
     * Returns the mod's player manager, which contains a player object for each online player.
     *
     * @return The player manager
     */
    public abstract PlayerManager getPlayerManager();

    /**
     * Creates a new item stack given an id, count, and NBT tag
     *
     * @param id    The type of item to create (e.g. minecraft:diamond)
     * @param count The count of the item stack
     * @param nbt   The NBT tag to be applied to the item stack
     *
     * @return The version
     */
    public abstract MItemStack createItem(Identifier id, int count, ConfigSection nbt);

    /**
     * Creates an Inventory GUI with a given title
     *
     * @param title  The title of the GUI
     * @return       The Inventory GUI
     */
    public abstract InventoryGUI createGUI(MComponent title);

    /**
     * Creates a Custom Scoreboard with a given title
     *
     * @param title The title of the scoreboard
     * @return      The scoreboard
     */
    public abstract CustomScoreboard createScoreboard(String id, MComponent title);

    /**
     * Executes a command in the server console
     *
     * @param command The command to run
     */
    public void executeConsoleCommand(String command) {
        executeConsoleCommand(command, true);
    }

    /**
     * Executes a command in the server console
     *
     * @param command The command to run
     * @param log     Whether the output should be logged or sent to admins
     */
    public abstract void executeConsoleCommand(String command, boolean log);



    /**
     * Retrieves the global Random object
     *
     * @return The global random object
     */
    public abstract Random getRandom();

    /**
     * Returns the global MidnightCoreAPI instance
     *
     * @return The global api
     */
    public static MidnightCoreAPI getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the global MidnightCoreAPI logger
     *
     * @return The logger
     */
    public static Logger getLogger() {
        return LOGGER;
    }

}