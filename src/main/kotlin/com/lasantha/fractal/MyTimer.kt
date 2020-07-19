package com.lasantha.fractal

import java.util.concurrent.atomic.AtomicLong

class MyTimer(private val name: String) {
    private val start = System.currentTimeMillis()
    private var last = AtomicLong(start)

    fun tick(stepName: String) {
        val now = System.currentTimeMillis()
        val prevLast = last.getAndSet(now);
        println("[$name - $stepName] time: ${now - prevLast} ms, total: ${now - start}")
    }
}
