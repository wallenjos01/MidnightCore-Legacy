package org.wallentines.midnightcore.spigot.adapter;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.MojangsonParser;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.ChatMessageType;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.ClientboundClearTitlesPacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.wallentines.mdcfg.codec.JSONCodec;
import org.wallentines.midnightcore.api.item.MItemStack;
import org.wallentines.midnightcore.api.text.MComponent;
import org.wallentines.mdcfg.ConfigSection;

import java.lang.reflect.Field;

public class Adapter_v1_17_R1 implements SpigotAdapter {

    private SkinUpdater_v1_17_R1 updater;

    private Field handle;

    public net.minecraft.world.item.ItemStack getHandle(org.bukkit.inventory.ItemStack is) {

        try {
            return (net.minecraft.world.item.ItemStack) handle.get(is);

        } catch (Exception ex) {
            return CraftItemStack.asNMSCopy(is);
        }
    }

    @Override
    public boolean init() {
        try {
            handle = CraftItemStack.class.getDeclaredField("handle");
            handle.setAccessible(true);

        } catch (Exception ex) {
            return false;
        }

        updater = new SkinUpdater_v1_17_R1();
        return updater.init();
    }

    @Override
    public GameProfile getGameProfile(Player pl) {
        return ((CraftPlayer) pl).getProfile();
    }

    @Override
    public void sendMessage(Player pl, MComponent comp) {

        EntityPlayer epl = ((CraftPlayer) pl).getHandle();
        epl.a(IChatBaseComponent.ChatSerializer.a(comp.toJSONString()), ChatMessageType.a, NIL_UUID);

    }

    @Override
    public void sendActionBar(Player pl, MComponent comp) {

        EntityPlayer epl = ((CraftPlayer) pl).getHandle();
        epl.a(IChatBaseComponent.ChatSerializer.a(comp.toJSONString()), ChatMessageType.c, NIL_UUID);

    }

    @Override
    public void sendTitle(Player pl, MComponent comp, int fadeIn, int stay, int fadeOut) {

        EntityPlayer epl = ((CraftPlayer) pl).getHandle();

        epl.b.sendPacket(new ClientboundSetTitleTextPacket(IChatBaseComponent.ChatSerializer.a(comp.toJSONString())));
        epl.b.sendPacket(new ClientboundSetTitlesAnimationPacket(fadeIn, stay, fadeOut));
    }

    @Override
    public void sendSubtitle(Player pl, MComponent comp, int fadeIn, int stay, int fadeOut) {

        EntityPlayer epl = ((CraftPlayer) pl).getHandle();
        epl.b.sendPacket(new ClientboundSetSubtitleTextPacket(IChatBaseComponent.ChatSerializer.a(comp.toJSONString())));
        epl.b.sendPacket(new ClientboundSetTitlesAnimationPacket(fadeIn, stay, fadeOut));
    }

    @Override
    public void clearTitles(Player pl) {

        EntityPlayer epl = ((CraftPlayer) pl).getHandle();
        epl.b.sendPacket(new ClientboundClearTitlesPacket(true));
    }

    @Override
    public void setTag(ItemStack is, ConfigSection sec) {

        net.minecraft.world.item.ItemStack mis = getHandle(is);

        try {
            NBTTagCompound cmp = MojangsonParser.parse(MItemStack.toNBT(sec));
            mis.setTag(cmp);

        } catch (CommandSyntaxException ex) {
            // Ignore
        }
    }

    @Override
    public ConfigSection getTag(ItemStack is) {

        net.minecraft.world.item.ItemStack mis = getHandle(is);

        NBTTagCompound cmp = mis.getTag();
        if(cmp == null) return null;

        return JSONCodec.loadConfig(cmp.asString()).asSection();
    }

    @Override
    public boolean hasOpLevel(Player pl, int lvl) {
        EntityPlayer epl = ((CraftPlayer) pl).getHandle();
        return epl.l(lvl);
    }

    @Override
    public ConfigSection getTag(Player pl) {
        EntityPlayer epl = ((CraftPlayer) pl).getHandle();
        NBTTagCompound tag = new NBTTagCompound();
        tag = epl.save(tag);

        return JSONCodec.loadConfig(tag.asString()).asSection();
    }

    @Override
    public void loadTag(Player pl, ConfigSection tag) {

        EntityPlayer epl = ((CraftPlayer) pl).getHandle();
        try {
            NBTTagCompound nbt = MojangsonParser.parse(MItemStack.toNBT(tag));
            epl.load(nbt);

        } catch (CommandSyntaxException ex) {
            // Ignore
        }
    }

    @Override
    public void addTickable(Runnable runnable) {
        CraftServer server = (CraftServer) Bukkit.getServer();
        server.getHandle().getServer().b(runnable);
    }

    @Override
    public SkinUpdater getSkinUpdater() {
        return updater;
    }

    @Override
    public ItemStack setupInternal(ItemStack item) {
        return CraftItemStack.asCraftCopy(item);
    }
}
