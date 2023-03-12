package code.blurone.farlander

import net.minecraft.world.level.levelgen.DensityFunctions
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator
import net.minecraft.world.level.levelgen.synth.BlendedNoise
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.craftbukkit.v1_19_R2.CraftWorld
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.world.WorldInitEvent
import org.bukkit.event.world.WorldLoadEvent
import org.bukkit.plugin.java.JavaPlugin


class Farlander : JavaPlugin(), Listener {
    override fun onEnable() {
        // Plugin startup logic
        saveDefaultConfig()
        for (world in Bukkit.getWorlds()) {
            prepareWorld(world)
        }

        Bukkit.getPluginManager().registerEvents(this, this)
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onWorldInit(event: WorldInitEvent) {
        prepareWorld(event.world)
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onWorldLoad(event: WorldLoadEvent) {
        prepareWorld(event.world)
    }

    private fun prepareWorld(world: World)
    {
        val noiseBasedChunkGenerator = (world as CraftWorld).handle.chunkSource.generator as NoiseBasedChunkGenerator
        val router = noiseBasedChunkGenerator.settings.value().noiseRouter
        // Get the BlendedNoise instance from somewhere inside noiseBasedChunkGenerator
        logger.info(world.name)
        (world as CraftWorld).handle.chunkSource.chunk


        logger.info((router.barrierNoise as DensityFunctions.HolderHolder).function.value().javaClass.canonicalName)
        logger.info((router.continents as DensityFunctions.HolderHolder).function.value().javaClass.canonicalName)
        logger.info((router.depth as DensityFunctions.HolderHolder).function.value().javaClass.canonicalName)
        logger.info((router.erosion as DensityFunctions.HolderHolder).function.value().javaClass.canonicalName)
        logger.info((router.finalDensity as DensityFunctions.HolderHolder).function.value().javaClass.canonicalName)
        logger.info((router.fluidLevelFloodednessNoise as DensityFunctions.HolderHolder).function.value().javaClass.canonicalName)
        logger.info((router.fluidLevelSpreadNoise as DensityFunctions.HolderHolder).function.value().javaClass.canonicalName)
        logger.info((router.initialDensityWithoutJaggedness as DensityFunctions.HolderHolder).function.value().javaClass.canonicalName)
        logger.info((router.lavaNoise as DensityFunctions.HolderHolder).function.value().javaClass.canonicalName)
        logger.info((router.ridges as DensityFunctions.HolderHolder).function.value().javaClass.canonicalName)
        logger.info((router.temperature as DensityFunctions.HolderHolder).function.value().javaClass.canonicalName)
        logger.info((router.vegetation as DensityFunctions.HolderHolder).function.value().javaClass.canonicalName)
        logger.info((router.veinGap as DensityFunctions.HolderHolder).function.value().javaClass.canonicalName)
        logger.info((router.veinRidged as DensityFunctions.HolderHolder).function.value().javaClass.canonicalName)
        logger.info((router.veinToggle as DensityFunctions.HolderHolder).function.value().javaClass.canonicalName)
    }
}