package com.lasantha.fractal.render

object ColorCoder {

    // 00000000,11111111,00000000,00000000
    private const val R_MASK = 16711680

    // 00000000,00000000,11111111,00000000
    private const val G_MASK = 65280

    // 00000000,00000000,00000000,11111111
    private const val B_MASK = 255

    fun encodeColor(r: Int, g: Int, b: Int): Int {
        return (r shl 16) or (g shl 8) or b
    }

    fun decodeColor(value: Int): Triple<Int, Int, Int> {
        val r = (value and R_MASK) shr 16
        val g = (value and G_MASK) shr 8
        val b = value and B_MASK
        return Triple(r, g, b)
    }
}