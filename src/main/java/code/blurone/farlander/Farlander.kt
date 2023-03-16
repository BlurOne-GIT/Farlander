package code.blurone.farlander

import it.unimi.dsi.fastutil.doubles.DoubleList
import net.minecraft.util.RandomSource
import net.minecraft.world.level.levelgen.DensityFunction
import net.minecraft.world.level.levelgen.DensityFunctions
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator
import net.minecraft.world.level.levelgen.synth.BlendedNoise
import net.minecraft.world.level.levelgen.synth.NormalNoise
import net.minecraft.world.level.levelgen.synth.PerlinNoise
import net.minecraft.world.level.levelgen.synth.SimplexNoise
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.craftbukkit.v1_19_R2.CraftWorld
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.world.WorldInitEvent
import org.bukkit.event.world.WorldLoadEvent
import org.bukkit.plugin.java.JavaPlugin
import java.lang.reflect.Field


class Farlander : JavaPlugin(), Listener {
    override fun onEnable() {
        // Plugin startup logic
        saveDefaultConfig()


        for (world in Bukkit.getWorlds()) {
            if (config.contains("worlds.${world.name}"))
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
        val randomSource = noiseBasedChunkGenerator.settings.value().randomSource.newInstance(world.seed)

        logger.info(world.name)

        prober(router.barrierNoise, randomSource, world.name, world.seed)
        prober(router.continents, randomSource, world.name, world.seed)
        prober(router.depth, randomSource, world.name, world.seed)
        prober(router.erosion, randomSource, world.name, world.seed)
        prober(router.finalDensity, randomSource, world.name, world.seed)
        prober(router.fluidLevelFloodednessNoise, randomSource, world.name, world.seed)
        prober(router.fluidLevelSpreadNoise, randomSource, world.name, world.seed)
        prober(router.initialDensityWithoutJaggedness, randomSource, world.name, world.seed)
        prober(router.lavaNoise, randomSource, world.name, world.seed)
        prober(router.ridges, randomSource, world.name, world.seed)
        prober(router.temperature, randomSource, world.name, world.seed)
        prober(router.vegetation, randomSource, world.name, world.seed)
        prober(router.veinGap, randomSource, world.name, world.seed)
        prober(router.veinRidged, randomSource, world.name, world.seed)
        prober(router.veinToggle, randomSource, world.name, world.seed)
    }

    private fun prober(origin: DensityFunction, randomSource: RandomSource, worldName: String, seed: Long)
    {
        //logger.info(origin.javaClass.canonicalName)
        for (field in origin.javaClass.declaredFields) {
            field.isAccessible = true
            val value = field.get(origin)
            if (value is BlendedNoise) return replacer(Pair(origin, field), randomSource, worldName, seed)
        }

        when (origin) {
            is DensityFunctions.HolderHolder -> {
                if (origin.function.value() is BlendedNoise) return replacer(Pair(origin.function, origin.function.javaClass.getDeclaredField("e")), randomSource, worldName, seed)
                return prober(origin.function.value(), randomSource, worldName, seed)
            }

            is DensityFunctions.MarkerOrMarked -> {
                return prober(origin.wrapped(), randomSource, worldName, seed)
            }

            is DensityFunctions.Spline -> return
        }
        if (origin.javaClass.canonicalName.endsWith("s.a")) {
            prober(origin.javaClass.getDeclaredField("f").apply { isAccessible = true }.get(origin) as DensityFunction, randomSource, worldName, seed)
            prober(origin.javaClass.getDeclaredField("g").apply { isAccessible = true }.get(origin) as DensityFunction, randomSource, worldName, seed)
            return
        }

        if (origin.javaClass.canonicalName.endsWith("s.d")) return
        if (origin.javaClass.canonicalName.endsWith("s.e")) return prober(origin.javaClass.getDeclaredField("a").apply { isAccessible = true }.get(origin) as DensityFunction, randomSource, worldName, seed)
        if (origin.javaClass.canonicalName.endsWith("s.f")) return

        when (origin.javaClass.simpleName)
        {
            "aa" -> return
            "g" -> return prober(origin.javaClass.getDeclaredField("e").apply { isAccessible = true }.get(origin) as DensityFunction, randomSource, worldName, seed)
            "h" -> return
            "i" -> return replacer(Pair(origin, origin.javaClass.getDeclaredField("f")), randomSource, worldName, seed)
            "k" -> return prober(origin.javaClass.getDeclaredField("e").apply { isAccessible = true }.get(origin) as DensityFunction, randomSource, worldName, seed)
            "l" -> return prober(origin.javaClass.getDeclaredField("e").apply { isAccessible = true }.get(origin) as DensityFunction, randomSource, worldName, seed)
            "o" -> {
                val noise: NormalNoise = (origin.javaClass.getDeclaredField("f").apply { isAccessible = true }.get(origin) as DensityFunction.NoiseHolder).noise
                    ?: return
                replacer(Pair(noise, origin.javaClass.getDeclaredField("d")), randomSource, worldName, seed)
                return replacer(Pair(noise, noise.javaClass.getDeclaredField("e")), randomSource, worldName, seed)
            }
            "q" -> return prober(origin.javaClass.getDeclaredField("f").apply { isAccessible = true }.get(origin) as DensityFunction, randomSource, worldName, seed)
            "v" -> {
                val noise: NormalNoise = (origin.javaClass.getDeclaredField("j").apply { isAccessible = true }.get(origin) as DensityFunction.NoiseHolder).noise
                    ?: return
                replacer(Pair(noise, origin.javaClass.getDeclaredField("d")), randomSource, worldName, seed)
                return replacer(Pair(noise, noise.javaClass.getDeclaredField("e")), randomSource, worldName, seed)
            }
        }

        throw Exception("Unhandled Density Function (inform developer): ${origin.javaClass.canonicalName}")
    }

    private fun replacer(pair: Pair<Any, Field>, randomSource: RandomSource, worldName: String, seed: Long)
    {
        pair.second.isAccessible = true
        val noise = pair.second.get(pair.first)
        logger.info(noise.javaClass.canonicalName)
        val highX: Int = config.getInt("world.$worldName.highX", 12550824)
        val highZ: Int = config.getInt("world.$worldName.highZ", 12550824)
        val lowX: Int = config.getInt("world.$worldName.lowX", 12550824)
        val lowZ: Int = config.getInt("world.$worldName.lowZ", 12550824)


        val farlandNoise: FarlandNoise = when (noise)
        {
            is BlendedNoise -> {
                logger.info("replacing blended noise")
                val minLimitNoise: PerlinNoise = noise.javaClass.getDeclaredField("g").apply { isAccessible = true }.get(noise) as PerlinNoise
                val maxLimitNoise: PerlinNoise = noise.javaClass.getDeclaredField("h").apply { isAccessible = true }.get(noise) as PerlinNoise
                val mainNoise: PerlinNoise = noise.javaClass.getDeclaredField("i").apply { isAccessible = true }.get(noise) as PerlinNoise
                val xzScale: Double = noise.javaClass.getDeclaredField("p").apply { isAccessible = true }.getDouble(noise)
                val yScale: Double = noise.javaClass.getDeclaredField("q").apply { isAccessible = true }.getDouble(noise)
                val xzFactor: Double = noise.javaClass.getDeclaredField("l").apply { isAccessible = true }.getDouble(noise)
                val yFactor: Double = noise.javaClass.getDeclaredField("m").apply { isAccessible = true }.getDouble(noise)
                val smearScaleMultiplier: Double = noise.javaClass.getDeclaredField("n").apply { isAccessible = true }.getDouble(noise)
                FarBlendedNoise(minLimitNoise, maxLimitNoise, mainNoise, xzScale, yScale, xzFactor, yFactor, smearScaleMultiplier, randomSource, highX, highZ, lowX, lowZ)
            }

            is PerlinNoise -> {
                logger.info("replacing perlin noise")
                val firstOctave: Int = noise.javaClass.superclass.getDeclaredField("c").apply { isAccessible = true }.getInt(this)
                val amplitudes = javaClass.superclass.getDeclaredField("d").apply { isAccessible = true }.get(this) as DoubleList
                FarPerlinNoise(randomSource, com.mojang.datafixers.util.Pair(firstOctave, amplitudes), true, highX, highZ, lowX, lowZ)
            }

            is SimplexNoise -> FarSimplexNoise(randomSource, highX, highZ, lowX, lowZ)

            else -> {
                logger.info("error replacing noise ${noise.javaClass.canonicalName} in ${pair.first.javaClass.canonicalName}::${pair.second.name}")
                return
            }
        }

        pair.second.set(pair.first, farlandNoise)
        logger.info("Replaced noise in ${pair.first.javaClass.canonicalName}::${pair.second.name} with ${farlandNoise.javaClass.canonicalName}")
    }
}