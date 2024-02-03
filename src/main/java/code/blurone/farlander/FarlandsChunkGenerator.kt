package code.blurone.farlander

import org.bukkit.Material
import org.bukkit.generator.ChunkGenerator
import org.bukkit.generator.WorldInfo
import java.util.*


class FarlandsChunkGenerator : ChunkGenerator() {
    override fun shouldGenerateSurface(): Boolean { return true }
    override fun shouldGenerateCaves(): Boolean { return true }
    override fun shouldGenerateDecorations(): Boolean { return true }
    override fun shouldGenerateMobs(): Boolean { return true }
    override fun shouldGenerateStructures(): Boolean { return true }

    override fun shouldGenerateNoise(): Boolean { return false }
    override fun shouldGenerateNoise(worldInfo: WorldInfo, random: Random, chunkX: Int, chunkZ: Int): Boolean {
        return chunkX < 0
    }

    override fun generateNoise(worldInfo: WorldInfo, random: Random, chunkX: Int, chunkZ: Int, chunkData: ChunkData) {
        if (shouldGenerateNoise(worldInfo, random, chunkX, chunkZ))
            return

        for (y in chunkData.minHeight..<chunkData.maxHeight) {
            for (x in 0..15) {
                for (z in 0..15) {
                    chunkData.setBlock(x, y, z, Material.STONE)
                }
            }
        }
    }
}