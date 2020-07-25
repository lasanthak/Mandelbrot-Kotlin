package com.lasantha.fractal.render.color

import com.lasantha.fractal.calculate.Result

class SimpleGrayColorCoder(override val maxN: Int) : ColorCoder {

    override fun toRGB(r: Result): Int {
        if (r.n >= maxN) {
            return ColorCoder.INSIDE_COLOR
        }

        val m = r.n % 255
        val gray = if ((r.n / 255) % 2 == 0) m else 255 - m

        return ColorCoder.encodeColor(gray, gray, gray)
    }
}
