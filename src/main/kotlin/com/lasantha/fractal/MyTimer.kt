package kotlin.com.lasantha.fractal

class MyTimer(private val name: String) {
    private val start = System.currentTimeMillis()

    fun tick() {
        println("$name time: ${System.currentTimeMillis() - start} ms")
    }
}
