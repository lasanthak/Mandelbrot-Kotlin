package com.lasantha.fractal.render.color

import com.lasantha.fractal.calc.PointResult

class SimpleGrayColorCoder(override val maxN: Int) : ColorCoder {

    override fun toRGB(result: PointResult<Double>): Int {
        if (result.n >= maxN) {
            return ColorCoder.INSIDE_COLOR
        }

        val m = result.n % 255
        val gray = if ((result.n / 255) % 2 == 0) m else 255 - m

        return ColorCoder.encodeColor(gray, gray, gray)
    }
}
