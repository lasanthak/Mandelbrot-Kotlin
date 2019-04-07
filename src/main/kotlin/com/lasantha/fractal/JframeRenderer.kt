package com.lasantha.fractal

import java.awt.*
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO
import javax.swing.JFrame
import javax.swing.JOptionPane
import kotlin.math.round


class JFrameRenderer(
    override val width: Int,
    override val height: Int,
    override val titleText: String
) : Renderer {

    private val frame = KJFrame(width, height, titleText)


    override fun render(matrix: Matrix<*>) {
        verifyDimensions(matrix)
        val timer = MyTimer("JFrame renderer")
        val g = frame.image.graphics

        matrix.foreach { value, x, y ->
            val grayCol = 255 - when (value) {
                -1 -> 0
                in 0..255 -> value
                in 256..450 -> 510 - value
                in 451..531 -> ((571 - value) / 2.toFloat()).toInt()
                in 532..559 -> ((589 - value) / 3.toFloat()).toInt()
                in 560..576 -> ((596 - value) / 4.toFloat()).toInt()
                else -> round(value / 10.0).toInt() % 256
            }
            g.color = Color(grayCol, grayCol, grayCol)
            g.fillRect(x, y, 1, 1)
        }

        frame.repaint()
        timer.tick()
    }

    fun setActionHandler(doAction: (x: Int, y: Int) -> Unit) {
        frame.setActionHandler(doAction)
    }

    private fun verifyDimensions(matrix: Matrix<*>) {
        if (matrix.pixelWidth > width || matrix.pixelHeight > height) {
            throw IllegalArgumentException("Invalid dimensions")
        }
    }

}

private class KJFrame(width: Int, height: Int, titleText: String) : JFrame(titleText) {
    val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)

    init {
        this.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        this.bounds = Rectangle(0, 0, width, height)

        this.addKeyListener(KJFrameKeyListener(this, ::saveImage))

        this.isVisible = true
        this.requestFocus()
    }

    override fun paint(g: Graphics?) {
        val g2 = g as Graphics2D
        g2.drawImage(image, null, 0, 0)
    }

    fun setActionHandler(doAction: (x: Int, y: Int) -> Unit) {
        this.addMouseListener(KJFrameMouseListener(this, doAction))
    }

    private fun saveImage() {
        val fileName = "${System.currentTimeMillis()}.png"
        try {
            ImageIO.write(image, "png", File(fileName))
            println("Image saved to: $fileName")
        } catch (ex: IOException) {
            throw RuntimeException(ex)
        }
    }
}

private class KJFrameKeyListener(private val parent: Component, private val doSave: () -> Unit) : KeyListener {
    override fun keyTyped(e: KeyEvent) {
    }

    override fun keyReleased(e: KeyEvent) {
    }

    override fun keyPressed(e: KeyEvent) {
        val special = e.isMetaDown || e.isControlDown
        if (special && (e.keyChar == 's' || e.keyChar == 'S')) {
            doSave()
        }
        if (e.keyChar == '?') {
            JOptionPane.showMessageDialog(parent, "Press Ctrl + s / Cmd + s to save")
        }
    }
}

private class KJFrameMouseListener(private val parent: Component, private val doAction: (x: Int, y: Int) -> Unit) : MouseListener {
    override fun mouseReleased(e: MouseEvent?) {
    }

    override fun mouseEntered(e: MouseEvent?) {
    }

    override fun mouseClicked(e: MouseEvent?) {
    }

    override fun mouseExited(e: MouseEvent?) {
    }

    override fun mousePressed(e: MouseEvent) {
        if (e.button == MouseEvent.BUTTON1 && e.clickCount == 2) {
            doAction(e.x, e.y)
        }
    }
}


