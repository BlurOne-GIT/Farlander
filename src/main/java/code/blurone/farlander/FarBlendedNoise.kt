package code.blurone.farlander

import net.minecraft.util.Mth
import net.minecraft.util.RandomSource
import net.minecraft.world.level.levelgen.DensityFunction.FunctionContext
import net.minecraft.world.level.levelgen.synth.BlendedNoise
import net.minecraft.world.level.levelgen.synth.ImprovedNoise
import net.minecraft.world.level.levelgen.synth.PerlinNoise

class FarBlendedNoise(
    private val minLimitNoise: PerlinNoise,
    private val maxLimitNoise: PerlinNoise,
    private val mainNoise: PerlinNoise,
    xzScale: Double,
    yScale: Double,
    private val xzFactor: Double,
    private val yFactor: Double,
    private val smearScaleMultiplier: Double,
    randomSource: RandomSource,
    override val highX: Int,
    override val highZ: Int,
    override val lowX: Int,
    override val lowZ: Int
    ) : BlendedNoise(randomSource, xzScale, yScale, xzFactor, yFactor, smearScaleMultiplier), FarlandNoise
{
    private val xzMultiplier: Double = 684.412 * xzScale
    private val yMultiplier: Double = 684.412 * yScale

    @Suppress("DEPRECATION")
    override fun compute(var0: FunctionContext): Double {
        val var1 = var0.blockX().toDouble() * xzMultiplier
        val var3 = var0.blockY().toDouble() * yMultiplier
        val var5 = var0.blockZ().toDouble() * xzMultiplier
        val var7 = var1 / this.xzFactor
        val var9 = var3 / this.yFactor
        val var11 = var5 / this.xzFactor
        val var13 = yMultiplier * this.smearScaleMultiplier
        val var15 = var13 / this.yFactor
        var var17 = 0.0
        var var19 = 0.0
        var var21 = 0.0
        var var24 = 1.0
        for (var26 in 0..7) {
            val var27 = mainNoise.getOctaveNoise(var26)
            if (var27 != null) {
                var21 += var27.noise(
                    PerlinNoise.wrap(var7 * var24),
                    PerlinNoise.wrap(var9 * var24),
                    PerlinNoise.wrap(var11 * var24),
                    var15 * var24,
                    var9 * var24
                ) / var24
            }
            var24 /= 2.0
        }
        val var26 = (var21 / 10.0 + 1.0) / 2.0
        val var28 = var26 >= 1.0
        val var29 = var26 <= 0.0
        var24 = 1.0
        for (var30 in 0..15) {
            val var31 = PerlinNoise.wrap(var1 * var24)
            val var33 = PerlinNoise.wrap(var3 * var24)
            val var35 = PerlinNoise.wrap(var5 * var24)
            val var37 = var13 * var24
            var var39: ImprovedNoise?
            if (!var28) {
                var39 = this.minLimitNoise.getOctaveNoise(var30)
                if (var39 != null) {
                    var17 += var39.noise(var31 * if (shallBreakX(var0.blockX())) 3137706 else 1, var33, var35 * if (shallBreakZ(var0.blockZ())) 3137706 else 1, var37, var3 * var24) / var24
                }
            }
            if (!var29) {
                var39 = this.maxLimitNoise.getOctaveNoise(var30)
                if (var39 != null) {
                    var19 += var39.noise(var31, var33, var35, var37, var3 * var24) / var24
                }
            }
            var24 /= 2.0
        }
        return Mth.clampedLerp(var17 / 512.0, var19 / 512.0, var26) / 128.0
    }
}