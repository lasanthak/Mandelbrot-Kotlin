package com.lasantha.fractal

import com.lasantha.fractal.matrix.DoubleMatrix
import com.lasantha.fractal.matrix.MatrixRange
import kotlinx.coroutines.*
import java.lang.Double.max


object FractalApp {
    private const val width = 1920
    private const val height = 1080
//    private const val width = 2880
//    private const val height = 1800

    private val jFrameRenderer = JFrameRenderer(width, height, "Mandelbrot Set")

    private var matrix = DoubleMatrix(width, height, -3.0, 1.25, 0.0025)
//    private var matrix = DoubleMatrix(width, height, -1.2121240234374997, 0.3170654296874999, 2.44140625E-6)
//    private var matrix = DoubleMatrix(width, height, -1.61375, 0.48624999999999996, 6.25E-4)
//    private var matrix = DoubleMatrix(width, height, -1.3728124999999998, 0.43531249999999994, 1.5625E-4)
//    private var matrix = DoubleMatrix(width, height, -1.2338281249999996, 0.3346093749999999, 3.90625E-5)
//    private var matrix = DoubleMatrix(width, height, -1.2146679687499997, 0.3211523437499999, 9.765625E-6)


    init {
        jFrameRenderer.setActionHandler(::doClickAction)
    }

    private fun doCalculation() = runBlocking {
        val mandelbrot = Mandelbrot(2000, 1000.0, 5)
        val calculate = { r: MatrixRange<Double> -> mandelbrot.calcMandelbrotDeep(r) }

        val jobs = mutableListOf<Job>()
        val timer = MyTimer("Calculation")

        repeat(height) { i ->
            // Each row is calculated in a single coroutine
            jobs += GlobalScope.launch(Dispatchers.Default) {
                matrix.transform(0, width - 1, i, i, calculate)
            }
        }

        println("Running ${jobs.size} coroutines")
        jobs.forEach { it.join() }
        timer.tick()

        jFrameRenderer.render(matrix)
    }

    private fun doClickAction(x: Int, y: Int) {
        val r1 = matrix.pixelToRange(x, y)
        val r2 = matrix.pixelToRange(x + width / 4, y + height / 4)

        val x1 = (r1.x1 + r1.x2) / 2
        val y1 = (r1.y1 + r1.y2) / 2

        val x2 = (r2.x1 + r2.x2) / 2
        val y2 = (r2.y1 + r2.y2) / 2

        val pixelSize = max((x2 - x1) / width, (y1 - y2) / height);

        println("DoubleMatrix(${width}, ${height}, ${x1}, ${y1}, ${pixelSize})")
        matrix = DoubleMatrix(width, height, x1, y1, pixelSize)
        doRun()
    }

    fun doRun() {
        doCalculation()
    }
}








