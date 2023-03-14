package org.wallentines.midnightcore.spigot.adapter;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.EnumHand;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.BiomeManager;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.wallentines.midnightcore.api.module.skin.Skin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class SkinUpdater_v1_19_R3 implements SkinUpdater {

    private Field fieldEntries;

    @Override
    public boolean init() {

        try {
            fieldEntries = ClientboundPlayerInfoUpdatePacket.class.getDeclaredField("b");
            fieldEntries.setAccessible(true);
        } catch (NoSuchFieldException ex) {
            ex.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public void updateSkin(Player spl, Skin skin) {

        EntityPlayer epl = ((CraftPlayer) spl).getHandle();

        MinecraftServer server = epl.c;
        if(server == null) return;

        // Create Packets
        ClientboundPlayerInfoRemovePacket remove = new ClientboundPlayerInfoRemovePacket(List.of(spl.getUniqueId()));
        ClientboundPlayerInfoUpdatePacket add = ClientboundPlayerInfoUpdatePacket.a(List.of(epl));

        applyActiveProfile(add, skin, server);

        List<Pair<EnumItemSlot, ItemStack>> items = new ArrayList<>();

        items.add(new Pair<>(EnumItemSlot.a, epl.b(EnumHand.a)));
        items.add(new Pair<>(EnumItemSlot.b, epl.b(EnumHand.b)));
        items.add(new Pair<>(EnumItemSlot.c, epl.c(EnumItemSlot.c)));
        items.add(new Pair<>(EnumItemSlot.d, epl.c(EnumItemSlot.d)));
        items.add(new Pair<>(EnumItemSlot.e, epl.c(EnumItemSlot.e)));
        items.add(new Pair<>(EnumItemSlot.f, epl.c(EnumItemSlot.f)));

        PacketPlayOutEntityEquipment equip = new PacketPlayOutEntityEquipment(epl.af(), items);

        PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(epl.af());
        PacketPlayOutNamedEntitySpawn spawn = new PacketPlayOutNamedEntitySpawn(epl);

        List<DataWatcher.b<?>> entityData = epl.aj().c();
        PacketPlayOutEntityMetadata tracker = null;
        if(entityData != null) {
            tracker = new PacketPlayOutEntityMetadata(epl.af(), entityData);
        }

        float headRot = spl.getEyeLocation().getYaw();
        int rot = (int) headRot;
        if (headRot < (float) rot) rot -= 1;
        PacketPlayOutEntityHeadRotation head = new PacketPlayOutEntityHeadRotation(epl, (byte) ((rot * 256.0F) / 360.0F));

        WorldServer world = (WorldServer) epl.H;

        PacketPlayOutRespawn respawn = new PacketPlayOutRespawn(
                world.Z(),
                world.ab(),
                BiomeManager.a(world.A()),
                epl.d.b(),
                epl.d.c(),
                world.ae(),
                world.z(),
                (byte) 3, // Preserve Metadata
                Optional.empty()
        );

        PacketPlayOutPosition position = new PacketPlayOutPosition(
                spl.getLocation().getX(),
                spl.getLocation().getY(),
                spl.getLocation().getZ(),
                spl.getLocation().getYaw(),
                spl.getLocation().getPitch(), new HashSet<>(), 0);

        // Send Packets
        for(EntityPlayer obs : server.ac().t()) {

            obs.b.a(remove);
            obs.b.a(add);

            if(obs == epl || !epl.H.equals(obs.H)) continue;

            obs.b.a(destroy);
            obs.b.a(spawn);
            obs.b.a(head);
            obs.b.a(equip);
            if(tracker != null) obs.b.a(tracker);

        }

        epl.b.a(respawn);
        epl.b.a(position);
        epl.b.a(equip);

        server.g(() -> {
            server.ac().d(epl);
            server.ac().e(epl);

            epl.w();

            spl.updateInventory();
        });
    }

    private void applyActiveProfile(ClientboundPlayerInfoUpdatePacket packet, Skin skin, MinecraftServer server) {

        List<ClientboundPlayerInfoUpdatePacket.b> entries = packet.d();

        ClientboundPlayerInfoUpdatePacket.b entry = null;

        int index = 0;
        for(; index < entries.size() ; index++) {
            ClientboundPlayerInfoUpdatePacket.b ent = entries.get(index);
            for(EntityPlayer u : server.ac().k) {
                if(u.cs().equals(ent.a())) {
                    entry = ent;
                    break;
                }
            }
        }
        if(entry == null) {
            return;
        }

        GameProfile profile = new GameProfile(entry.a(), entry.b().getName());

        EntityPlayer epl = server.ac().a(entry.a());
        if(epl != null) {
            profile.getProperties().clear();
            profile.getProperties().putAll(epl.fI().getProperties());
        }
        if(skin != null) {
            profile.getProperties().get("textures").clear();
            profile.getProperties().put("textures", new Property("textures", skin.getValue(), skin.getSignature()));
        }

        List<ClientboundPlayerInfoUpdatePacket.b> newEntries = new ArrayList<>(entries);
        newEntries.set(index - 1, new ClientboundPlayerInfoUpdatePacket.b(
                profile.getId(),
                profile,
                entry.c(),
                entry.d(),
                entry.e(),
                entry.f(),
                entry.g()
        ));

        try {
            fieldEntries.set(packet, List.copyOf(newEntries));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
