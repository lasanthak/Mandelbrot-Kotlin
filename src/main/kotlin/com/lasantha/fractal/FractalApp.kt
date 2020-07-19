package com.lasantha.fractal

import com.lasantha.fractal.calculate.Mandelbrot
import com.lasantha.fractal.matrix.DoubleMatrix
import com.lasantha.fractal.render.ColorCoder
import com.lasantha.fractal.render.JFrameRenderer
import kotlinx.coroutines.*
import java.lang.Double.max
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.math.*


object FractalApp {
    private const val twoPI = 2 * PI
    private val rFactor = 1 / ln(2.0)
    private val gFactor = 1 / (3 * sqrt(2.0) * ln(2.0))
    private val bFactor = 1 / (7 * 3.0.pow(0.125) * ln(2.0))

    private val df: DecimalFormat = DecimalFormat("#,###")
    private var zoomFactor = BigDecimal.ONE

    private const val w = 1920
    private const val h = 1080
//    private const val w = 2880
//    private const val h = 1800

//    private var matrix = DoubleMatrix(w, h, -3.0, 1.25, 0.0025)
//    private var matrix = DoubleMatrix(w, h, -1.61375, 0.48625, 6.25E-4)
//    private var matrix = DoubleMatrix(w, h, -1.3728125, 0.4353125, 1.5625E-4)
//    private var matrix = DoubleMatrix(w, h, -1.233828125, 0.334609375, 3.90625E-5)
//    private var matrix = DoubleMatrix(w, h, -1.21466796875, 0.32115234375, 9.765625E-6)
//    private var matrix = DoubleMatrix(w, h, -1.927921142578, 3.7231445312492777E-4, 6.103515625E-7)
//    private var matrix = DoubleMatrix(w, h, -1.9353922718009557, -2.5250200999817516E-6, 1.455191580660801E-13)
//    private var matrix = DoubleMatrix(w, h, -1.93542839050293, 2.0065307617046905E-5, 3.8146972656388775E-8)
//    private var matrix = DoubleMatrix(w, h, 0.4379185144138036, 0.3418942276032618, 3.556825615928742E-17)
//    private var matrix = DoubleMatrix(w, h, 0.43791851441382945, 0.34189422760324895, 8.90491384334761E-18)
//    private var matrix = DoubleMatrix(w, h, 0.4379185144116993, 0.3418942276044753, 2.273747034275141E-15)
//    private var matrix = DoubleMatrix(w, h, 0.4379185144132852, 0.3418942276035124, 5.684753079793625E-16)
//    private var matrix = DoubleMatrix(w, h, 0.4437215614318849, 0.3642622566223145, 9.53674316410362E-9)
//    private var matrix = DoubleMatrix(w, h, -0.9578436348773535, 0.272229713592678, 9.313225769284432E-12)
//    private var matrix = DoubleMatrix(w, h, -0.9578436619788404, 0.2722297281771896, 3.725290307713773E-11)
//    private var matrix = DoubleMatrix(w, h, -0.9578437647223468, 0.2722297826409338, 1.4901161196160622E-10)
//    private var matrix = DoubleMatrix(w, h, -0.957844210863113, 0.27223002314567546, 5.960464477950256E-10)
//    private var matrix = DoubleMatrix(w, h, -0.9578589820861813, 0.272234363555908, 9.53674316410362E-9)
//    private var matrix = DoubleMatrix(w, h, -0.95788703918457, 0.2722489166259764, 3.814697265627313E-8)
//    private var matrix = DoubleMatrix(w, h, -0.9579977416992185, 0.2723043823242186, 1.525878906250411E-7)
//    private var matrix = DoubleMatrix(w, h, -0.9584484863281248, 0.27252563476562486, 6.103515625000231E-7)
//    private var matrix = DoubleMatrix(w, h, -0.6711912869894877, 0.4583694873703642, 2.3283064776580675E-12)
//    private var matrix = DoubleMatrix(w, h, -0.9578436278225849, 0.2722297113062811, 2.3283064776580675E-12)
    private var matrix = DoubleMatrix(w, h, -1.1980964751602863, 0.31521355852393285, 2.2736956350610384E-15)

    private val jFrameRenderer = JFrameRenderer(w, h, "Mandelbrot Set")
    init {
        jFrameRenderer.zoomInHandler(::doZoomIn)
        df.decimalFormatSymbols = DecimalFormatSymbols(Locale.getDefault())
    }

    private fun doCalculation() = runBlocking {
        val maxN = 2000
        val escapeRadius = 1000.0
        val samplesSqrt = 3
        val blendingFactor = 6.7 //111, 6.7, 4.3
        val mandelbrot = Mandelbrot(maxN, escapeRadius, samplesSqrt)

        val jobs = mutableListOf<Job>()
        val timer = MyTimer("Calculation")

        repeat(h) { y ->
            // Each row is calculated in a single coroutine
            jobs += GlobalScope.launch(Dispatchers.Default) {
                // x: from 0 to w -1
                for (x in 0 until w) {
                    val range = matrix.pixelToRange(x, y)
                    var r = 0
                    var g = 0
                    var b = 0
                    //var gray = 0
                    mandelbrot.calculate(range) { n, rSquare ->
                        if (n < maxN) {
                            val v = ln(ln(rSquare) / 2.0.pow(n)) / blendingFactor
                            //gray = round(127.5 * (1 + cos(twoPI * v))).toInt()
                            r = round(127.5 * (1 - cos(rFactor * v))).toInt()
                            g = round(127.5 * (1 - cos(gFactor * v))).toInt()
                            b = round(127.5 * (1 - cos(bFactor * v))).toInt()
                        }
                    }
                    matrix.set(x, y, ColorCoder.encodeColor(r, g, b))
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

        val pixelSize = max((x2 - x1) / w, (y1 - y2) / h)
        matrix = DoubleMatrix(w, h, x1, y1, pixelSize)
        zoomFactor = zoomFactor.multiply(BigDecimal.valueOf(4))


        println("┈┈┈┈┈┈┈┈┈< Zooming ${df.format(zoomFactor)}x >┈┈┈┈┈┈┈┈┈")
        println("($w, $h, $x1, $y1, $pixelSize)")

        doCalculation()
    }

    fun run() {
        doCalculation()
    }
}
