package com.lasantha.fractal.render.color

/**
 * The sRGB integer encodes RGB components into a single integer.
 * These are the bit position indices (inclusive):
 * R: [23, 16]
 * G: [15, 8]
 * B: [7, 0]
 * The range for the RGB component values is from 0 to 255 (inclusive).
 */
interface ColorCoder {
    val maxN: Int

    /**
     * @param n Number of iteration
     * @param rxr Square of escape radius (for the point when escapes)
     * @return The RGB encoded integer for the color
     */
    fun toRGB(n: Int, rxr: Double): Int

    /**
     * Encodes a given red, green, and blue components (with range 0 to 255)
     * into and sRGB encoded integer.
     */
    fun encodeColor(r: Int, g: Int, b: Int): Int {
        return (r shl 16) or (g shl 8) or b
    }

    /**
     * Decodes an integer that has RGB components encoded in as an sRGB
     * integer into it's individual components.
     */
    fun decodeColor(value: Int): Triple<Int, Int, Int> {
        val r = (value and R_MASK) shr 16
        val g = (value and G_MASK) shr 8
        val b = value and B_MASK
        return Triple(r, g, b)
    }

    companion object {
        // 00000000,11111111,00000000,00000000
        const val R_MASK = 0x00ff0000

        // 00000000,00000000,11111111,00000000
        const val G_MASK = 0x0000ff00

        // 00000000,00000000,00000000,11111111
        const val B_MASK = 0x000000ff

        // Default color (black) for non-escaping points (i.e. within the set)
        const val INSIDE_COLOR = 0x01000000
    }
}