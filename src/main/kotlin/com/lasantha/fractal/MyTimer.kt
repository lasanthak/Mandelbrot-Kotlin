package com.lasantha.fractal

import java.util.concurrent.atomic.AtomicLong

class MyTimer(private val name: String) {
    private val start = System.currentTimeMillis()
    private var last = AtomicLong(start)

    fun logDelta(stepName: String) {
        val now = System.currentTimeMillis()
        val prevLast = last.getAndSet(now);

        val time = now - prevLast
        val total = now - start
        val timeString = "[$name - $stepName] time: $time ms"
        if (time == total) {
            println(timeString)
        } else {
            println("$timeString, total: $total")
        }
    }
}
