package code.blurone.farlander

import com.mojang.datafixers.util.Pair
import it.unimi.dsi.fastutil.doubles.DoubleList
import net.minecraft.util.RandomSource
import net.minecraft.world.level.levelgen.synth.ImprovedNoise
import net.minecraft.world.level.levelgen.synth.PerlinNoise

class FarlandNoise(var0: RandomSource?, var1: Pair<Int, DoubleList>?, var2: Boolean) : PerlinNoise(var0, var1, var2) {

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
        var var13 = javaClass.superclass.getDeclaredField("f").apply { isAccessible = true }.getDouble(this)
        var var15 = javaClass.superclass.getDeclaredField("e").apply { isAccessible = true }.getDouble(this)
        val noiseLevels = javaClass.superclass.getDeclaredField("b").apply { isAccessible = true }.get(this) as Array<ImprovedNoise?>
        val amplitudes = javaClass.superclass.getDeclaredField("d").apply { isAccessible = true }.get(this) as DoubleList

        for (var17 in noiseLevels.indices) {
            val var18 = noiseLevels[var17]
            if (var18 != null) {
                val var19 = var18.noise(
                    wrap(var0 * var13) * if (var0 > 6144 || var0 < -6144) 3137706 else 1,
                    if (var10) -var18.yo else wrap(var2 * var13),
                    wrap(var4 * var13) * if (var4 > 6144 || var0 < -6144) 3137706 else 1,
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