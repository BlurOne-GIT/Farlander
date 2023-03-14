package code.blurone.farlander

import it.unimi.dsi.fastutil.doubles.DoubleList
import net.minecraft.core.Holder
import net.minecraft.world.level.levelgen.DensityFunction
import net.minecraft.world.level.levelgen.DensityFunctions
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator
import net.minecraft.world.level.levelgen.synth.BlendedNoise
import net.minecraft.world.level.levelgen.synth.NormalNoise
import net.minecraft.world.level.levelgen.synth.PerlinNoise
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.World.Environment
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
        if (world.environment == Environment.NORMAL) {
            val worldNormalNoise: NormalNoise? =
                ((((router.continents as DensityFunctions.HolderHolder).function.value() as DensityFunctions.MarkerOrMarked).wrapped() as DensityFunctions.HolderHolder).function.value()
                    .let {
                        return@let it.javaClass.getDeclaredField("j").apply { isAccessible = true }.get(it)
                    } as DensityFunction.NoiseHolder).noise //ESTO DA NULO NO SE POR QUE

            if (worldNormalNoise == null) {
                logger.info("worldNormalNoise is null")
                return
            }

            val oldPerlinNoise: PerlinNoise = worldNormalNoise.javaClass.getDeclaredField("d").apply { isAccessible = true }.get(worldNormalNoise) as PerlinNoise
            val firstOctave: Int = oldPerlinNoise.javaClass.superclass.getDeclaredField("c").apply { isAccessible = true }.getInt(oldPerlinNoise)
            val amplitudes: DoubleList = oldPerlinNoise.javaClass.superclass.getDeclaredField("d").apply { isAccessible = true }.get(oldPerlinNoise) as DoubleList
            worldNormalNoise.javaClass.getDeclaredField("d").apply { isAccessible = true }.set(worldNormalNoise, FarlandNoise(noiseBasedChunkGenerator.settings.value().randomSource.newInstance(world.seed), com.mojang.datafixers.util.Pair(firstOctave, amplitudes), !noiseBasedChunkGenerator.settings.value().useLegacyRandomSource))
        }
        logger.info(world.name)
        /*
        logger.info(prober(router.barrierNoise)?.javaClass?.canonicalName)
        logger.info(prober(router.continents)?.javaClass?.canonicalName)
        logger.info(prober(router.depth)?.javaClass?.canonicalName)
        logger.info(prober(router.erosion)?.javaClass?.canonicalName)
        logger.info(prober(router.finalDensity)?.javaClass?.canonicalName)
        logger.info(prober(router.fluidLevelFloodednessNoise)?.javaClass?.canonicalName)
        logger.info(prober(router.fluidLevelSpreadNoise)?.javaClass?.canonicalName)
        logger.info(prober(router.initialDensityWithoutJaggedness)?.javaClass?.canonicalName)
        logger.info(prober(router.lavaNoise)?.javaClass?.canonicalName)
        logger.info(prober(router.ridges)?.javaClass?.canonicalName)
        logger.info(prober(router.temperature)?.javaClass?.canonicalName)
        logger.info(prober(router.vegetation)?.javaClass?.canonicalName)
        logger.info(prober(router.veinGap)?.javaClass?.canonicalName)
        logger.info(prober(router.veinRidged)?.javaClass?.canonicalName)
        logger.info(prober(router.veinToggle)?.javaClass?.canonicalName)
        */

        /*
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
        */
    }

    private fun prober(origin: DensityFunction): Any?
    {
        for (field in origin.javaClass.declaredFields) {
            val past = field.isAccessible
            field.isAccessible = true
            val value = field.get(origin)
            if (value is DensityFunction.NoiseHolder)
                return value.noise

            if (value.javaClass.name.contains("DensityFunction."))
                return value
            else if (value is DensityFunction)
                return prober(value)

            if (value is Holder<*>)
                return prober(value.value() as DensityFunction)
            field.isAccessible = past
        }
        return origin
    }
}