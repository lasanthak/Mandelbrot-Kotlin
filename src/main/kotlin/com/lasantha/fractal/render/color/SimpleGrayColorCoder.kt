package com.lasantha.fractal.render.color

class SimpleGrayColorCoder(override val maxN: Int) : ColorCoder {

    override fun toRGB(n: Int, rxr: Double): Int {
        if (n >= maxN) {
            return ColorCoder.DEFAULT_COLOR
        }

        val m = n % 255
        val gray = if ((n / 255) % 2 == 0) m else 255 - m

        return encodeColor(gray, gray, gray)
    }
}