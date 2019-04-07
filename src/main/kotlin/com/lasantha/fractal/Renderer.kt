package com.lasantha.fractal


interface Renderer {
    val width: Int
    val height: Int
    val titleText: String

    fun render(matrix: Matrix<*>)
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
                    -1 -> "."
                    in 0..255 -> value.toString()
                    else -> "#"
                }
            )
            if (x == width - 1) println()
        }
    }

}
