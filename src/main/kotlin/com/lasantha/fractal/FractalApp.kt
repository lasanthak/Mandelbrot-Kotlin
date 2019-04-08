package com.lasantha.fractal

import kotlinx.coroutines.*


object FractalApp {
    // 1920 x 1080
//    private const val width = 1920
//    private const val height = 1080

    //    val range = MatrixRange(-2.0, 0.2, -1.2, 1.2)
//    val range = MatrixRange(0.16125, 0.17725, 0.638438, 0.646438)
//    val range = MatrixRange(0.3495, 0.3514, 0.3495, 0.3505)
//    val range = MatrixRange(0.35045, 0.35065, 0.3505, 0.3506)
//    val range = MatrixRange(0.16299615, 0.16312222, 0.6361975, 0.6362611)
//    val range = MatrixRange(0.16304703763020836, 0.16306104540798613, 0.6362304839392361, 0.6362383633142361)
//    private val range = MatrixRange(0.16305052498321762, 0.16305519424247691, 0.6362338326736111, 0.6362364591319445)
//    private val range = MatrixRange(0.16305239959455708, 0.16305239983178005, 0.6362354616846979, 0.6362354618181358)
//    private val range = MatrixRange(0.1630165744064358, 0.16301663205161185, 0.6362499602626672, 0.6362499926880788)

    // 2880 x 1800
    private const val width = 2880
    private const val height = 1800
//    private val range = MatrixRange(0.16301657433333333, 0.163016681, 0.6362499561111111, 0.6362500227777779)
//    private val range = MatrixRange(0.16302385913541667, 0.16303786691319447, 0.6362058171180556, 0.6362145719791666)
    private val range = MatrixRange(0.16303113122377195, 0.16303113834046036, 0.6362083869319972, 0.6362083909351345)

    private val doubleMatrix = DoubleMatrix(width, height).applyRange(range)

    private val jFrameRenderer = JFrameRenderer(width, height, "Mandelbrot")

    init {
        jFrameRenderer.setActionHandler(::doClickAction)
    }

    private fun doCalculation() = runBlocking {
        //        val calculate = { r: MatrixRange<Double> -> Mandelbrot.calcMandelbrot(r) }
        val calculate = { r: MatrixRange<Double> -> Mandelbrot.calcMandelbrotDeep(r, 5) }

        val jobs = mutableListOf<Job>()
        val timer = MyTimer("Calculation")

        repeat(height) { i ->
            // Each row is calculated in a single coroutine
            jobs += GlobalScope.launch(Dispatchers.Default) {
                doubleMatrix.transform(0, width - 1, i, i, calculate)
            }
        }

        println("Running ${jobs.size} coroutines")
        jobs.forEach { it.join() }
        timer.tick()

        jFrameRenderer.render(doubleMatrix)
    }

    private fun doClickAction(x: Int, y: Int) {
        val r1 = doubleMatrix.toRange(x, y)
        val r2 = doubleMatrix.toRange(x + width / 4, y + height / 4)

        println("MatrixRange(${r1.x1}, ${r2.x1}, ${r1.y1}, ${r2.y1})")
        doubleMatrix.applyRange(MatrixRange(r1.x1, r2.x1, r1.y1, r2.y1))

        doRun()
    }

    fun doRun() {
        doCalculation()
    }
}








