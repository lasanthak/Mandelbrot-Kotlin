package com.lasantha.fractal

import com.lasantha.fractal.calc.DoublePointCalculator
import com.lasantha.fractal.calc.FractalType
import com.lasantha.fractal.calc.JuliaSet
import com.lasantha.fractal.calc.MandelbrotSet
import com.lasantha.fractal.calc.PointResult
import com.lasantha.fractal.matrix.DoubleMatrix
import com.lasantha.fractal.matrix.DoubleMatrix.Companion.toMapSquare
import com.lasantha.fractal.matrix.MapPoint
import com.lasantha.fractal.render.JFrameRenderer
import com.lasantha.fractal.render.Renderer
import com.lasantha.fractal.render.color.GrayColorCoder
import com.lasantha.fractal.render.color.RGBColorCoder
import com.lasantha.fractal.render.color.Simple3DColorCoder
import com.lasantha.fractal.render.color.SimpleGrayColorCoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow


object MandelbrotApp {
    private const val maxIterations = 1000
    private const val escapeRadius = 10.0
    private const val subPixelCountSqrt = 3 // 1 for just midpoint, 2 for 4 point, 3 for 9 points, etc.

    private var zoomFactor = 1
    private const val blendingFactor = 5.45656 //111.0, 7.389 (e^2), 6.7, 5.45656 (2e), 4.3, 2.71828 (e)

    private const val w = 1920 // 1920, 2880, 3840
    private const val h = 1080 // 1080, 1800, 2160

    private val matrix = DoubleMatrix(w, h)
    private val pointCalculator = DoublePointCalculator(maxIterations, escapeRadius)

    private val fractalType = FractalType.MANDELBROT_SET

    private val fractal = if (fractalType == FractalType.MANDELBROT_SET)
        MandelbrotSet(matrix, pointCalculator)
    else
        JuliaSet(matrix, pointCalculator, c = MapPoint(-0.835, -0.2321))

    private val startingMidPoint =
        if (fractalType == FractalType.MANDELBROT_SET) MapPoint(-0.75, 0.0) else MapPoint(0.0, 0.0)
    private var midPointSquare = toMapSquare(startingMidPoint, max(2.7 / h, 4.7 / w), subPixelCountSqrt)

    // Interesting C points for Julia Set
    //  (-0.8, 0.156) // (blending factor > 300)
    //  (-0.835, -0.2321)
    //  (-0.7269, 0.1889)
    //  (-0.4, 0.6)
    //  (-0.4, -0.59)
    //  (-0.8, 0.156)
    //  (0.285, 0.01)
    //  (-0.74543, 0.11301) // (blending factor > 212) *
    //  (-0.77, 0.13)
    //  (-0.1, 0.651)
    //  (0.235, 0.01)
    //  (0.35, 0.35)
    //  (0.4, 0.4)
    //  (-0.54, 0.54)
    //  (0.28, 0.008)

    //private var midPointSquare = toMapSquare(MapPoint(-1.1613719455979288, 0.29056722398498075), 5.82076670813731E-13, subPixelCountSqrt)
    // Interesting areas of Mandelbrot Set
    //   (-1.2228125, 0.3509375), 1.5625E-4
    //   (-1.2561284722222223, 0.38069444444444445), 5.787037037037038E-6
    //   (-1.1613729858398436, 0.29056549072265603), 1.5258789062509252E-7
    //   (-1.1613719461672, 0.2905672235228119), 9.313225769284432E-12
    //   (-1.196328125, 0.31351562499999996), 3.90625E-5
    //   (-1.20529296875, 0.31587890625000004), 9.765625E-6
    //   (-1.9273352050780002, 4.272460937492775E-5), 6.103515625E-7
    //   (-1.9353922716612573, -2.5250986803271072E-6), 1.455191580660801E-13
    //   (-1.9353917694091798, -5.340576174030348E-7), 3.8146972656388775E-8
    //   (0.4379185144138377, 0.3418942276032426), 3.556825615928742E-17
    //   (0.437918514413838, 0.3418942276032441), 8.90491384334761E-17
    //   (0.43791851441388213, 0.3418942276032475), 2.273747034275141E-15
    //   (0.43791851441383095, 0.3418942276032054), 5.684753079793625E-16
    //   (0.44373071670532244, 0.36425710678100587), 9.53674316410362E-9
    //   (-0.9578436259366567, 0.27222970856353607), 9.313225769284432E-12
    //   (-0.9578436262160535, 0.27222970806062197), 3.725290307713773E-11
    //   (-0.9578436216711993, 0.2722297021746633), 1.4901161196160622E-10
    //   (-0.9578436386585231, 0.27222970128059365), 5.960464477950256E-10
    //   (-0.957843625346819, 0.27222970823446885), 6.622738308833618E-11
    //   (-0.9578498268127437, 0.2722292137145994), 9.53674316410362E-9
    //   (-0.9578504180908199, 0.272228317260742), 3.814697265627313E-8
    //   (-0.9578512573242184, 0.2722219848632811), 1.525878906250411E-7
    //   (-0.9578625488281248, 0.2721960449218748), 6.103515625000231E-7
    //   (-0.6711912847543134, 0.4583694861130787), 2.3283064776580675E-12
    //   (-0.9578436255874107, 0.2722297100489956), 2.3283064776580675E-12
    //   (-1.1980964751581036, 0.31521355852270505), 2.2736956350610384E-15
    //   (0.26141138106584555, 0.001961407959461164), 1.4901161193992217E-10
    //   (-1.1613719455979288, 0.29056722398498075), 5.82076670813731E-13

    private val colorCoders = listOf(
        SimpleGrayColorCoder(maxIterations),
        Simple3DColorCoder(maxIterations),
        RGBColorCoder(maxIterations, blendingFactor),
        GrayColorCoder(maxIterations, blendingFactor)
    )
    private var colorCoderIndex = 0
    private val renderer: Renderer<PointResult<Double>> = JFrameRenderer(w, h, "Mandelbrot Set")


    init {
        renderer.zoomInHandler(::doZoomIn)
        renderer.reRenderHandler(::doReRender)
    }

    private fun doParallelCalculations() = runBlocking {
        val jobs = mutableListOf<Job>()

        val noOfCoroutines = 540
        val rowsPerCoroutine = ceil(h.toDouble() / noOfCoroutines).toInt()
        repeat(noOfCoroutines) { i ->
            val start = i * rowsPerCoroutine
            val end = min(start + rowsPerCoroutine, h)
            jobs += GlobalScope.launch(Dispatchers.Default) {
                for (y in start until end) { // start to end -1
                    for (x in 0 until w) { // 0 to w -1
                        fractal.calculatePixel(x, y, midPointSquare)
                    }
                }
            }
        }

        println("Running ${jobs.size} coroutines")
        jobs.forEach { it.join() }
    }

    private fun doCalculateAndRender() {
        val timer = MyTimer("Fractal")
        renderer.indicateBusy(true)

        doParallelCalculations()
        timer.logDelta("Calculation")

        renderer.render(matrix, colorCoders[colorCoderIndex])
        timer.logDelta("Render")

        renderer.indicateBusy(false)
    }

    private fun doReRender() {
        val timer = MyTimer("Fractal")
        renderer.indicateBusy(true)

        // cycle through the list of color coders
        colorCoderIndex = (colorCoderIndex + 1) % colorCoders.size
        renderer.render(matrix, colorCoders[colorCoderIndex])
        timer.logDelta("Render")

        renderer.indicateBusy(false)
    }

    /**
     * Zooms into a 1/4 of a region (i.e. 4x magnification).
     */
    private fun doZoomIn(x: Int, y: Int, w: Int, h: Int) {
        val newMidPoint = matrix.pixelMidPoint(x, y, midPointSquare)
        val newPixelSize = midPointSquare.size * w.toDouble() / matrix.width.toDouble()
        midPointSquare = toMapSquare(newMidPoint, newPixelSize, subPixelCountSqrt)
        zoomFactor++

        println("┈┈┈┈┈┈┈┈┈[ Zoom: $zoomFactor X, Magnification: ${4.0.pow(zoomFactor)} X ]┈┈┈┈┈┈┈┈┈")
        println("($w, $h, ${newMidPoint.x}, ${newMidPoint.y}, $newPixelSize)")
        doCalculateAndRender()
    }

    fun doRun() {
        doCalculateAndRender()
    }
}
