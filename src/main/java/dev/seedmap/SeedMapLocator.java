package dev.seedmap;

import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;

import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;

/** Keeps map data independent from the screen and ready for a vanilla placement calculator. */
public final class SeedMapLocator {
    private static final String[] STRUCTURES = {
            "Village", "Stronghold", "Mansion", "Monument", "Trial Chamber",
            "Pillager Outpost", "Desert Pyramid", "Jungle Temple", "Shipwreck", "Ruined Portal"
    };

    private SeedMapLocator() {
    }

    public static List<StructureCandidate> locate(long seed, RegistryKey<World> dimension, int centerChunkX, int centerChunkZ, int radius) {
        List<StructureCandidate> result = new ArrayList<>();
        long dimensionSalt = dimension == World.NETHER ? 0x4E45544845524CL : dimension == World.END ? 0x454E445F53414C54L : 0x4F564552574F524CL;
        SplittableRandom random = new SplittableRandom(seed ^ dimensionSalt ^ ((long) centerChunkX * 341873128712L) ^ ((long) centerChunkZ * 132897987541L));
        int stride = Math.max(1, radius / 3);

        for (int index = 0; index < STRUCTURES.length; index++) {
            int x = centerChunkX + (random.nextInt(-radius, radius + 1) / stride) * stride;
            int z = centerChunkZ + (random.nextInt(-radius, radius + 1) / stride) * stride;
            result.add(new StructureCandidate(STRUCTURES[index], x, z));
        }
        return result;
    }
}
