package code.blurone.farlander

import net.minecraft.util.RandomSource
import net.minecraft.world.level.levelgen.synth.SimplexNoise

class FarSimplexNoise(randomSource: RandomSource,
                      override val highX: Int,
                      override val highZ: Int,
                      override val lowX: Int,
                      override val lowZ: Int
) : SimplexNoise(randomSource), FarlandNoise {
    override fun getValue(var0: Double, var2: Double): Double {
        return super.getValue(var0 * if (shallBreakX((var0/2).toInt())) 3137706 else 1, var2 * if (shallBreakZ((var2/2).toInt())) 3137706 else 1)
    }
}