package dev.seedmap;

public record StructureCandidate(String name, int chunkX, int chunkZ) {
    public int blockX() {
        return chunkX * 16 + 8;
    }

    public int blockZ() {
        return chunkZ * 16 + 8;
    }
}
