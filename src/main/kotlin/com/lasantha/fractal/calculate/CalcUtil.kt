package com.lasantha.fractal.calculate


object CalcUtil {
}

/**
 * @param n Number of iteration
 * @param rxr Square of escape radius (for the point when escapes)
 * @param z Escape point
 * @param der Derivative
 */
data class Result(val n: Int, val rxr: Double, val z: Pair<Double, Double>, val der: Pair<Double, Double>)
