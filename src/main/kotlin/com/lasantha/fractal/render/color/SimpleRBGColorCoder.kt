package com.lasantha.fractal.render.color

class SimpleRBGColorCoder(override val maxN: Int) : ColorCoder {

    override fun toRGB(n: Int, rxr: Double): Int {
        if (n >= maxN) {
            return ColorCoder.DEFAULT_COLOR
        }

        val m = n % 0x00ffffff
        return if ((n / 0x00ffffff) % 2 == 0) m else 0x00ffffff - m
    }
}