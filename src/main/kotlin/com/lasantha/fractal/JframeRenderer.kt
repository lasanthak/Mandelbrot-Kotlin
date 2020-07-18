@file:Suppress("unused")

package com.lasantha.fractal

import com.lasantha.fractal.matrix.Matrix
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Rectangle
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

class JFrameRenderer(
    override val width: Int,
    override val height: Int,
    override val titleText: String
) : Renderer {

    private val frame = KJFrame(width, height, titleText)
    private val rectangle = Rectangle(0, 0, width/4, height/4)

    private var doZoomIn: ((x: Int, y: Int) -> Unit)? = null

    override fun render(matrix: Matrix<*>) {
        verifyDimensions(matrix)
        val timer = MyTimer("JFrame renderer")
        val g = frame.image.graphics

        matrix.foreach { value, x, y ->
            val d = value / 255
            val m = value % 255

            val grayCol = if (d % 2 == 0) m else (255 - m)
            g.color = Color(grayCol, grayCol, grayCol)
            g.fillRect(x, y, 1, 1)
        }

        frame.repaint()
        timer.tick()
    }

    override fun zoomInHandler(doZoomIn: (x: Int, y: Int) -> Unit) {
        this.doZoomIn = doZoomIn
    }

    private fun verifyDimensions(matrix: Matrix<*>) {
        if (matrix.pixelWidth > width || matrix.pixelHeight > height) {
            throw IllegalArgumentException("Invalid dimensions")
        }
    }

    private fun saveImage() {
        val fileName = "${System.currentTimeMillis()}.png"
        try {
            ImageIO.write(frame.image, "png", File(fileName))
            println("Image saved to: $fileName")
        } catch (ex: IOException) {
            throw RuntimeException(ex)
        }
    }

    init {
        frame.addKeyListener(object : KeyListener {
            override fun keyTyped(e: KeyEvent) {
                if (e.keyChar == 'S') {
                    saveImage()
                }

                if (e.keyChar == '?') {
                    JOptionPane.showMessageDialog(frame, "Press S to save")
                }
            }

            override fun keyPressed(e: KeyEvent) {}
            override fun keyReleased(e: KeyEvent) {}
        })

        frame.addMouseListener(object : MouseListener {
            override fun mouseClicked(e: MouseEvent) {
                if (e.button == MouseEvent.BUTTON1 && e.clickCount == 2) {
                    doZoomIn?.invoke(e.x, e.y)
                }
            }

            override fun mousePressed(e: MouseEvent) {
                rectangle.setLocation(e.x, e.y)
                frame.rectangle = rectangle
                frame.repaint()
            }

            override fun mouseReleased(e: MouseEvent) {
                frame.rectangle = null
                frame.repaint()
            }

            override fun mouseEntered(e: MouseEvent) {}
            override fun mouseExited(e: MouseEvent) {}
        })
    }
}

private class KJFrame(width: Int, height: Int, titleText: String) : JFrame(titleText) {
    val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)

    var rectangle: Rectangle? = null

    init {
        this.defaultCloseOperation = EXIT_ON_CLOSE
        this.bounds = Rectangle(0, 0, width, height)
        this.isVisible = true
        this.requestFocus()
    }

    override fun paint(g: Graphics?) {
        val g2 = g as Graphics2D
        g2.drawImage(image, null, 0, 0)

        val r = rectangle
        if (r != null) {
            g2.color = Color.YELLOW
            g2.draw(r)
        }
    }
}



