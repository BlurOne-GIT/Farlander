package code.blurone.farlander

interface FarlandNoise {
    val highX: Int
    val highZ: Int
    val lowX: Int
    val lowZ: Int

    fun shallBreakX(x: Int): Boolean {
        return x > highX || x < lowX
    }

    fun shallBreakZ(z: Int): Boolean {
        return z > highZ || z < lowZ
    }
}