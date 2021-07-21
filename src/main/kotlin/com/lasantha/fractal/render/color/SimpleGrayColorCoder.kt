package com.lasantha.fractal.render.color

import com.lasantha.fractal.calculate.Result

class SimpleGrayColorCoder(override val maxN: Int) : ColorCoder {

    override fun toRGB(res: Result): Int {
        if (res.n >= maxN) {
            return ColorCoder.INSIDE_COLOR
        }

        val m = res.n % 255
        val gray = if ((res.n / 255) % 2 == 0) m else 255 - m

        return ColorCoder.encodeColor(gray, gray, gray)
    }
}
