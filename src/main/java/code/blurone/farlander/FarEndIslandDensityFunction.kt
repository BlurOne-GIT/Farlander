package code.blurone.farlander

import com.mojang.serialization.MapCodec
import net.minecraft.util.KeyDispatchDataCodec
import net.minecraft.util.Mth
import net.minecraft.util.RandomSource
import net.minecraft.world.level.levelgen.DensityFunction
import net.minecraft.world.level.levelgen.DensityFunction.FunctionContext
import net.minecraft.world.level.levelgen.DensityFunctions.EndIslandDensityFunction
import net.minecraft.world.level.levelgen.LegacyRandomSource
import net.minecraft.world.level.levelgen.synth.SimplexNoise

class FarEndIslandDensityFunction(var0: Long, override val highX: Int, override val highZ: Int,
                                  override val lowX: Int, override val lowZ: Int) : DensityFunction.SimpleFunction, FarlandNoise {
    //val CODEC: KeyDispatchDataCodec<EndIslandDensityFunction?> =
    //    KeyDispatchDataCodec.of(MapCodec.unit(EndIslandDensityFunction(0L)))
    private val ISLAND_THRESHOLD = -0.9f
    private val islandNoise: SimplexNoise

    init {
        val var2: RandomSource = LegacyRandomSource(var0)
        var2.consumeCount(17292)
        islandNoise = SimplexNoise(var2)
    }

    private fun getHeightValue(var0: SimplexNoise?, var1: Int, var2: Int): Float {
        val var3 = var1 / 2
        val var4 = var2 / 2
        val var5 = var1 % 2
        val var6 = var2 % 2
        var var7 = 100.0f - Mth.sqrt((var1 * var1 + var2 * var2).toFloat()) * 8.0f
        var7 = Mth.clamp(var7, -100.0f, 80.0f)
        for (var8 in -12..12) {
            for (var9 in -12..12) {
                val var10 = (var3 + var8).toLong()
                val var12 = (var4 + var9).toLong()
                if (var10 * var10 + var12 * var12 > 4096L && var0!!.getValue(
                        var10.toDouble(),
                        var12.toDouble()
                    ) < -0.8999999761581421
                ) {
                    val var14 = (Mth.abs(var10.toFloat()) * 3439.0f + Mth.abs(var12.toFloat()) * 147.0f) % 13.0f + 9.0f
                    val var15 = (var5 - var8 * 2).toFloat()
                    val var16 = (var6 - var9 * 2).toFloat()
                    var var17 = 100.0f - Mth.sqrt(var15 * var15 + var16 * var16) * var14
                    var17 = Mth.clamp(var17, -100.0f, 80.0f)
                    var7 = Math.max(var7, var17)
                }
            }
        }
        return var7
    }

    override fun compute(var0: FunctionContext): Double {
        return (getHeightValue(islandNoise, var0.blockX() / 8, var0.blockZ() / 8).toDouble() - 8.0) / 128.0
    }

    override fun minValue(): Double {
        return -0.84375
    }

    override fun maxValue(): Double {
        return 0.5625
    }

    override fun codec(): KeyDispatchDataCodec<out DensityFunction> {
        return CODEC
    }
}