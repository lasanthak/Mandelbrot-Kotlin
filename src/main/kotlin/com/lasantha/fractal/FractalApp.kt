package com.lasantha.fractal

import com.lasantha.fractal.matrix.DoubleMatrix
import kotlinx.coroutines.*
import java.lang.Double.max
import kotlin.math.*


object FractalApp {
    private const val twoPI = 2* PI

    private const val w = 1920
    private const val h = 1080
//    private const val w = 2880
//    private const val h = 1800

//    private var matrix = DoubleMatrix(w, h, -3.0, 1.25, 0.0025)
//    private var matrix = DoubleMatrix(w, h, -1.2121240234375, 0.3170654296875, 2.44140625E-6)
//    private var matrix = DoubleMatrix(w, h, -1.61375, 0.48625, 6.25E-4)
//    private var matrix = DoubleMatrix(w, h, -1.3728125, 0.4353125, 1.5625E-4)
//    private var matrix = DoubleMatrix(w, h, -1.233828125, 0.334609375, 3.90625E-5)
//    private var matrix = DoubleMatrix(w, h, -1.21466796875, 0.32115234375, 9.765625E-6)
//    private var matrix = DoubleMatrix(w, h, -1.927921142578, 3.7231445312492777E-4, 6.103515625E-7)
//    private var matrix = DoubleMatrix(w, h, -1.9353922718009557, -2.5250200999817516E-6, 1.455191580660801E-13)
    private var matrix = DoubleMatrix(w, h, -1.93542839050293, 2.0065307617046905E-5, 3.8146972656388775E-8)

    private val jFrameRenderer = JFrameRenderer(w, h, "Mandelbrot Set")
    init {
        jFrameRenderer.zoomInHandler(::doZoomIn)
    }

    private fun doCalculation() = runBlocking {
        val maxN = 2000
        val escapeRadius = 1000.0
        val samplesSqrt = 5
        val smoothFactor = 4.3
        val mandelbrot = Mandelbrot(maxN, escapeRadius, samplesSqrt)

        val jobs = mutableListOf<Job>()
        val timer = MyTimer("Calculation")

        repeat(h) { y ->
            // Each row is calculated in a single coroutine
            jobs += GlobalScope.launch(Dispatchers.Default) {
                // x: from 0 to w -1
                for (x in 0 until w) {
                    val range = matrix.pixelToRange(x, y)
                    var value = 0
                    mandelbrot.calculate(range) { n, rSquare ->
                        if (n < maxN) {
                            val v = ln(rSquare) / 2.0.pow(n)
                            value = round(127.5 * (1 + cos(twoPI * ln(v) / smoothFactor))).toInt()
                        }
                    }
                    matrix.set(x, y, value)
                }
            }
        }

        println("Running ${jobs.size} coroutines")
        jobs.forEach { it.join() }
        timer.tick()

        jFrameRenderer.render(matrix)
    }

    private fun doZoomIn(x: Int, y: Int) {
        val r1 = matrix.pixelToRange(x, y)
        val r2 = matrix.pixelToRange(x + w / 4, y + h / 4)

        val x1 = (r1.x1 + r1.x2) / 2
        val y1 = (r1.y1 + r1.y2) / 2

        val x2 = (r2.x1 + r2.x2) / 2
        val y2 = (r2.y1 + r2.y2) / 2

        val pixelSize = max((x2 - x1) / w, (y1 - y2) / h);

        println("DoubleMatrix(w, h, ${x1}, ${y1}, ${pixelSize})")
        matrix = DoubleMatrix(w, h, x1, y1, pixelSize)
        doCalculation()
    }

    fun doRun() {
        doCalculation()
    }
}
