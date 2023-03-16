package code.blurone.farlander

import com.mojang.datafixers.util.Pair
import it.unimi.dsi.fastutil.doubles.DoubleList
import net.minecraft.util.RandomSource
import net.minecraft.world.level.levelgen.synth.ImprovedNoise
import net.minecraft.world.level.levelgen.synth.PerlinNoise
import kotlin.math.pow

class FarPerlinNoise(var0: RandomSource, var1: Pair<Int, DoubleList>, var2: Boolean, override val highX: Int,
                     override val highZ: Int, override val lowX: Int, override val lowZ: Int) : PerlinNoise(var0, var1, var2), FarlandNoise {

    private val firstOctave: Int = var1.first
    private val amplitudes: DoubleList = var1.second
    private val lowestFreqInputFactor: Double = 2.0.pow((firstOctave).toDouble())
    private val lowestFreqValueFactor: Double = 2.0.pow((this.amplitudes.size - 1).toDouble()) / (2.0.pow(this.amplitudes.size.toDouble()) - 1.0)
    private val noiseLevels: Array<ImprovedNoise?> = arrayOfNulls(this.amplitudes.size)

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun getValue(
        var0: Double,
        var2: Double,
        var4: Double,
        var6: Double,
        var8: Double,
        var10: Boolean
    ): Double {
        var var11 = 0.0
        var var13 = lowestFreqInputFactor
        var var15 = lowestFreqValueFactor

        for (var17 in noiseLevels.indices) {
            val var18 = noiseLevels[var17]
            if (var18 != null) {
                val var19 = var18.noise(
                    wrap(var0 * var13) * if (shallBreakX(var0.toInt())) 3137706 else 1,
                    if (var10) -var18.yo else wrap(var2 * var13),
                    wrap(var4 * var13) * if (shallBreakZ(var4.toInt())) 3137706 else 1,
                    var6 * var13,
                    var8 * var13
                )
                var11 += amplitudes.getDouble(var17) * var19 * var15
            }
            var13 *= 2.0
            var15 /= 2.0
        }

        return var11
    }
}