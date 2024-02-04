package code.blurone.farlander

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.generator.ChunkGenerator
import org.bukkit.generator.WorldInfo
import org.bukkit.util.noise.PerlinNoiseGenerator
import java.util.*


class FarlandsChunkGenerator : ChunkGenerator() {
    private val logger = Bukkit.getPluginManager().getPlugin("Farlander")!!.logger

    override fun shouldGenerateSurface(): Boolean { return true }
    override fun shouldGenerateCaves(): Boolean { return true }
    override fun shouldGenerateDecorations(): Boolean { return true }
    override fun shouldGenerateMobs(): Boolean { return true }
    override fun shouldGenerateStructures(): Boolean { return true }

    override fun shouldGenerateNoise(): Boolean { return false }
    override fun shouldGenerateNoise(worldInfo: WorldInfo, random: Random, chunkX: Int, chunkZ: Int): Boolean {
        return chunkX > 0
    }

    override fun generateNoise(worldInfo: WorldInfo, random: Random, chunkX: Int, chunkZ: Int, chunkData: ChunkData) {
        if (shouldGenerateNoise(worldInfo, random, chunkX, chunkZ))
            return

        val noiseGenerator = PerlinNoiseGenerator(random)

        for (y in chunkData.minHeight..<chunkData.maxHeight) {
            for (z in 0..15) {
                val noise = noiseGenerator.noise(2147483647.0, y*.125, (z+chunkZ*16)*.25, 3, 0.5, 2.0)
                if (noise >= 0.0)
                    for (x in 0..15) {
                        chunkData.setBlock(x, y, z, Material.STONE)
                    }
            }
        }
    }
}