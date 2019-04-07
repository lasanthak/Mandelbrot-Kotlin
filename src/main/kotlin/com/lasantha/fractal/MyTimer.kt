package com.lasantha.fractal

class MyTimer(val name: String) {
    val start = System.currentTimeMillis()

    fun tick() {
        println("$name time: ${System.currentTimeMillis() - start} ms")
    }
}
