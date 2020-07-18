package com.lasantha.fractal

import com.lasantha.fractal.matrix.Matrix

interface Renderer<T> {
    val width: Int
    val height: Int
    val titleText: String

    fun render(matrix: Matrix<*, T>)

    fun zoomInHandler(doZoomIn: (x: Int, y: Int) -> Unit)
}
