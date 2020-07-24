package com.lasantha.fractal

import com.lasantha.fractal.calculate.Mandelbrot
import com.lasantha.fractal.matrix.DoubleMatrix
import com.lasantha.fractal.render.JFrameRenderer
import com.lasantha.fractal.render.Renderer
import com.lasantha.fractal.render.color.ColorCoder
import com.lasantha.fractal.render.color.GrayColorCoder
import com.lasantha.fractal.render.color.RGBColorCoder
import com.lasantha.fractal.render.color.SimpleGrayColorCoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.Double.max
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.min
import kotlin.math.pow


object MandelbrotApp {
    private val df: DecimalFormat = DecimalFormat("#,###")
    private var zoomFactor = 1

    private const val maxN = 2000
    private const val escapeRadius = 1000.0
    private const val samplesSqrt = 3 // 5, 4
    private const val blendingFactor = 5.45656 //111.0, 7.389 (e^2), 6.7, 5.45656 (2e), 4.3, 2.71828 (e)

    private const val w = 1920
    private const val h = 1080
//    private const val w = 2880
//    private const val h = 1800

    // Mandelbrot Set
    private var matrix = DoubleMatrix(w, h, -0.5, 0.0, 0.0025)
//    private var matrix = DoubleMatrix(w, h, -1.2228125, 0.3509375, 1.5625E-4)
//    private var matrix = DoubleMatrix(w, h, -1.1613729858398436, 0.29056549072265603, 1.5258789062509252E-7)
//    private var matrix = DoubleMatrix(w, h, -1.1613719461672, 0.2905672235228119, 9.313225769284432E-12)
//    private var matrix = DoubleMatrix(w, h, -1.196328125, 0.31351562499999996, 3.90625E-5)
//    private var matrix = DoubleMatrix(w, h, -1.20529296875, 0.31587890625000004, 9.765625E-6)
//    private var matrix = DoubleMatrix(w, h, -1.9273352050780002, 4.272460937492775E-5, 6.103515625E-7)
//    private var matrix = DoubleMatrix(w, h, -1.9353922716612573, -2.5250986803271072E-6, 1.455191580660801E-13)
//    private var matrix = DoubleMatrix(w, h, -1.9353917694091798, -5.340576174030348E-7, 3.8146972656388775E-8)
//    private var matrix = DoubleMatrix(w, h, 0.4379185144138377, 0.3418942276032426, 3.556825615928742E-17)
//    private var matrix = DoubleMatrix(w, h, 0.437918514413838, 0.3418942276032441, 8.90491384334761E-18)
//    private var matrix = DoubleMatrix(w, h, 0.43791851441388213, 0.3418942276032475, 2.273747034275141E-15)
//    private var matrix = DoubleMatrix(w, h, 0.43791851441383095, 0.3418942276032054, 5.684753079793625E-16)
//    private var matrix = DoubleMatrix(w, h, 0.44373071670532244, 0.36425710678100587, 9.53674316410362E-9)
//    private var matrix = DoubleMatrix(w, h, -0.9578436259366567, 0.27222970856353607, 9.313225769284432E-12)
//    private var matrix = DoubleMatrix(w, h, -0.9578436262160535, 0.27222970806062197, 3.725290307713773E-11)
//    private var matrix = DoubleMatrix(w, h, -0.9578436216711993, 0.2722297021746633, 1.4901161196160622E-10)
//    private var matrix = DoubleMatrix(w, h, -0.9578436386585231, 0.27222970128059365, 5.960464477950256E-10)
//    private var matrix = DoubleMatrix(w, h, -0.9578498268127437, 0.2722292137145994, 9.53674316410362E-9)
//    private var matrix = DoubleMatrix(w, h, -0.9578504180908199, 0.272228317260742, 3.814697265627313E-8)
//    private var matrix = DoubleMatrix(w, h, -0.9578512573242184, 0.2722219848632811, 1.525878906250411E-7)
//    private var matrix = DoubleMatrix(w, h, -0.9578625488281248, 0.2721960449218748, 6.103515625000231E-7)
//    private var matrix = DoubleMatrix(w, h, -0.6711912847543134, 0.4583694861130787, 2.3283064776580675E-12)
//    private var matrix = DoubleMatrix(w, h, -0.9578436255874107, 0.2722297100489956, 2.3283064776580675E-12)
//    private var matrix = DoubleMatrix(w, h, -1.1980964751581036, 0.31521355852270505, 2.2736956350610384E-15)
//    private var matrix = DoubleMatrix(w, h, 0.26141138106584555, 0.001961407959461164, 1.4901161193992217E-10)
//    private var matrix = DoubleMatrix(w, h, -1.1613719455979288, 0.29056722398498075, 5.82076670813731E-13)


    // Julia set
//    private var matrix = DoubleMatrix(w, h, 0.0, 0.0, 0.0025)
//    private val cPoint = Pair(-0.8, 0.156) // (blending factor > 300)
//    private val cPoint = Pair(-0.7269, 0.1889)
//    private val cPoint = Pair(-0.4, 0.6)
//    private val cPoint = Pair(-0.8, 0.156)
//    private val cPoint = Pair(0.285, 0.01)
    private val cPoint = Pair(-0.74543, 0.11301) // (blending factor > 212) ***
//    private val cPoint = Pair(-0.75, 0.11)
//    private val cPoint = Pair(-0.1, 0.651)

    private val colorCoders = listOf(
            RGBColorCoder(maxN, blendingFactor),
            SimpleGrayColorCoder(maxN),
            GrayColorCoder(maxN, blendingFactor)
    )
    private var colorCoderIndex = 0

    private val renderer: Renderer<Int> = JFrameRenderer(w, h, "Mandelbrot Set")

    private val mandelbrot = Mandelbrot(maxN, escapeRadius, samplesSqrt)

    init {
        df.decimalFormatSymbols = DecimalFormatSymbols(Locale.getDefault())
        renderer.zoomInHandler(::doZoomIn)
        renderer.reRenderHandler(::doReRender)
    }

    private fun doParallelCalculations() = runBlocking {
        val jobs = mutableListOf<Job>()

        val noOfCoroutines = h/2
        val rowsPerCoroutine = ceil(h.toDouble() / noOfCoroutines).toInt()
        repeat(noOfCoroutines) { i ->
            val start = i * rowsPerCoroutine
            val end = min((i + 1) * rowsPerCoroutine, h)
            jobs += GlobalScope.launch(Dispatchers.Default) {
                for (y in start until end) { // start to end -1
                    for (x in 0 until w) { // 0 to w -1
                        // If the point is within the set (ie. already calculated), no need to re-calculate
                        if (matrix.get(x, y) != ColorCoder.INSIDE_COLOR) {
                            val rangeForPoint = matrix.pixelToRange(x, y)
                            mandelbrot.calculateMandelbrotSet(rangeForPoint) { n, rxr -> matrix.set(x, y, toColor(n, rxr)) }
//                        mandelbrot.calculateJuliaSet(rangeForPoint, cPoint) { n, rxr -> matrix.set(x, y, toColor(n, rxr)) }
                        }
                    }
                }
            }
        }

        println("Running ${jobs.size} coroutines")
        jobs.forEach { it.join() }
    }

    private fun toColor(n:Int, rxr:Double): Int {
        return colorCoders[colorCoderIndex].toRGB(n, rxr)
    }

    private fun doCalculateAndRender() {
        val timer = MyTimer("Mandelbrot")
        renderer.indicateBusy(true)

        doParallelCalculations()
        timer.tick("Calculation")

        renderer.render(matrix)
        timer.tick("Render")

        renderer.indicateBusy(false)
    }

    private fun doReRender() {
        // cycle through the list of color coders
        colorCoderIndex = (colorCoderIndex + 1) % colorCoders.size
        doCalculateAndRender()
    }

    /**
     * Zooms into a 1/4 of a region (i.e. 4x magnification).
     */
    private fun doZoomIn(x: Int, y: Int) {
        val r1 = matrix.pixelToRange(x - w / 8, y - h / 8)
        val x1 = (r1.x1 + r1.x2) / 2
        val y1 = (r1.y1 + r1.y2) / 2

        val r2 = matrix.pixelToRange(x + w / 8, y + h / 8)
        val x2 = (r2.x1 + r2.x2) / 2
        val y2 = (r2.y1 + r2.y2) / 2

        val midX = (x1 + x2) / 2
        val midY = (y1 + y2) / 2
        val pixelSize = max((x2 - x1) / w, (y1 - y2) / h)

        matrix = DoubleMatrix(w, h, midX, midY, pixelSize)
        zoomFactor++

        println("┈┈┈┈┈┈┈┈┈[ ${zoomFactor}x Zoom, ${df.format(4.0.pow(zoomFactor))}x Magnification]┈┈┈┈┈┈┈┈┈")
        println("($w, $h, $midX, $midY, $pixelSize)")
        doCalculateAndRender()
    }

    fun doRun() {
        doCalculateAndRender()
    }
}
