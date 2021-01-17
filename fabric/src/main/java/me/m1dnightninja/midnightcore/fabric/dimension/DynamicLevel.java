package me.m1dnightninja.midnightcore.fabric.dimension;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.saveddata.maps.MapIndex;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.ServerLevelData;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.Executor;

public class DynamicLevel extends ServerLevel {


    public DynamicLevel(MinecraftServer minecraftServer, Executor executor, LevelStorageSource.LevelStorageAccess levelStorageAccess, ServerLevelData serverLevelData, ResourceKey<Level> resourceKey, DimensionType dimensionType, ChunkProgressListener chunkProgressListener, ChunkGenerator chunkGenerator, boolean bl, long l, List<CustomSpawner> list, boolean bl2) {
        super(minecraftServer, executor, levelStorageAccess, serverLevelData, resourceKey, dimensionType, chunkProgressListener, chunkGenerator, bl, l, list, bl2);
    }

    @Override
    public @Nullable MapItemSavedData getMapData(String id) {
        return getDataStorage().get(() -> new MapItemSavedData(id), id);
    }

    @Override
    public void setMapData(MapItemSavedData mapState) {
        getDataStorage().set(mapState);
    }

    @Override
    public int getFreeMapId() {
        return getDataStorage().computeIfAbsent(MapIndex::new, "idcounts").getFreeAuxValueForMap();
    }
}
