package com.lasantha.fractal

import com.lasantha.fractal.matrix.Matrix

interface Renderer {
    val width: Int
    val height: Int
    val titleText: String

    fun render(matrix: Matrix<*>)

    fun zoomInHandler(doZoomIn: (x: Int, y: Int) -> Unit)
}

class SimpleRenderer(
        override val width: Int,
        override val height: Int,
        override val titleText: String
) : Renderer {

    override fun render(matrix: Matrix<*>) {
        matrix.foreach { value, x, y ->
            print(
                    when (value) {
                        in 0..255 -> value.toString()
                        else -> "#"
                    }
            )
            if (x == width - 1) println()
        }
    }

    override fun zoomInHandler(doZoomIn: (x: Int, y: Int) -> Unit) {
        println("Zoom in is not supported")
    }
}
