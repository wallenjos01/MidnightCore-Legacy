package org.wallentines.midnightcore.fabric.server;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@SuppressWarnings("unused")
@ParametersAreNonnullByDefault
public class EmptyGenerator extends ChunkGenerator {

    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Codec<EmptyGenerator> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                            EmptyGeneratorSettings.CODEC.fieldOf("settings").forGetter(gen -> gen.settings)
                    )
                    .apply(instance, instance.stable(EmptyGenerator::new))
    );

    private final EmptyGeneratorSettings settings;

    public EmptyGenerator(EmptyGeneratorSettings settings) {

        super(new FixedBiomeSource(settings.getBiome()));
        this.settings = settings;
    }

    @Override
    protected @NotNull Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public void applyCarvers(WorldGenRegion worldGenRegion, long l, RandomState randomState, BiomeManager biomeManager, StructureManager structureManager, ChunkAccess chunkAccess, GenerationStep.Carving carving) { }

    @Override
    public void buildSurface(WorldGenRegion worldGenRegion, StructureManager structureManager, RandomState randomState, ChunkAccess chunkAccess) { }

    @Override
    public void spawnOriginalMobs(WorldGenRegion worldGenRegion) { }

    @Override
    public void createStructures(RegistryAccess registryAccess, ChunkGeneratorStructureState chunkGeneratorStructureState, StructureManager structureManager, ChunkAccess chunkAccess, StructureTemplateManager structureTemplateManager) { }

    @Override
    public @NotNull CompletableFuture<ChunkAccess> fillFromNoise(Executor executor, Blender blender, RandomState randomState, StructureManager structureManager, ChunkAccess chunkAccess) {

        return CompletableFuture.supplyAsync(() -> {

            if(chunkAccess.getPos().x == 0 && chunkAccess.getPos().z == 0) {
                chunkAccess.setBlockState(new BlockPos(0,63,0), Blocks.BEDROCK.defaultBlockState(), true);
            }
            return chunkAccess;
        });
    }

    @Override
    public int getSpawnHeight(LevelHeightAccessor levelHeightAccessor) {
        return 64;
    }

    @Override
    public int getGenDepth() {
        return 384;
    }

    @Override
    public int getSeaLevel() {
        return -63;
    }

    @Override
    public int getMinY() {
        return -64;
    }

    @Override
    public int getBaseHeight(int x, int z, Heightmap.Types types, LevelHeightAccessor levelHeightAccessor, RandomState randomState) {
        return levelHeightAccessor.getMinBuildHeight();
    }

    @Override
    public @NotNull NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor levelHeightAccessor, RandomState randomState) {

        BlockState[] states = new BlockState[levelHeightAccessor.getHeight()];
        Arrays.fill(states, Blocks.AIR.defaultBlockState());

        return new NoiseColumn(levelHeightAccessor.getMinBuildHeight(), states);
    }

    @Override
    public void addDebugScreenInfo(List<String> list, RandomState randomState, BlockPos blockPos) { }

    public static EmptyGenerator create(ResourceKey<Biome> biome, RegistryAccess.Frozen frozen) {

        Registry<Biome> reg = frozen.registryOrThrow(Registries.BIOME);
        Optional<Holder<Biome>> holder = reg.getHolder(biome).map(val -> val);

        return new EmptyGenerator(new EmptyGeneratorSettings(holder, reg.getHolderOrThrow(Biomes.PLAINS)));
    }

    public static class EmptyGeneratorSettings {

        public static final Codec<EmptyGeneratorSettings> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Biome.CODEC.optionalFieldOf("biome").orElseGet(Optional::empty).forGetter(settings -> Optional.of(settings.biome)),
                        RegistryOps.retrieveElement(Biomes.PLAINS)
                ).apply(instance, EmptyGeneratorSettings::new));

        private final Holder<Biome> biome;

        public Holder<Biome> getBiome() {
            return biome;
        }

        @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
        public EmptyGeneratorSettings(Optional<Holder<Biome>> biome, Holder<Biome> defaultBiome) {

            this.biome = biome.orElseGet(() -> {
                LOGGER.warn("Unknown biome requested, defaulting for plains");
                return defaultBiome;
            });
        }
    }

}
