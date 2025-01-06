package com.lasantha.fractal.render

import com.lasantha.fractal.matrix.Matrix
import com.lasantha.fractal.render.color.ColorCoder

interface Renderer<T> {
    val width: Int
    val height: Int
    val titleText: String

    fun render(matrix: Matrix<*, T>, colorCoder: ColorCoder)

    fun zoomInHandler(doZoomIn: (x: Int, y: Int, w: Int, h: Int) -> Unit)

    fun reRenderHandler(doReRender: () -> Unit)

    fun indicateBusy(busy: Boolean)
}
