@file:Suppress("unused")

package com.lasantha.fractal.render

import com.lasantha.fractal.matrix.Matrix
import java.awt.Color
import java.awt.Cursor
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
) : Renderer<Int> {

    private val frame = KJFrame(width, height, titleText)
    private val rectangle = Rectangle(0, 0, width / 4, height / 4)
    private var doZoomIn: ((x: Int, y: Int) -> Unit)? = null
    private var doReRender: (() -> Unit)? = null

    override fun render(matrix: Matrix<*, Int>) {
        if (matrix.widthInPixels > width || matrix.heightInPixels > height) {
            throw IllegalArgumentException("Invalid dimensions")
        }
        matrix.forEach { sRGBValue, x, y -> frame.image.setRGB(x, y, sRGBValue) }
        frame.repaint()
    }

    override fun zoomInHandler(doZoomIn: (x: Int, y: Int) -> Unit) {
        this.doZoomIn = doZoomIn
    }

    override fun reRenderHandler(doReRender: () -> Unit) {
        this.doReRender = doReRender
    }

    override fun indicateBusy(busy: Boolean) {
        frame.cursor = if (busy) Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) else Cursor.getDefaultCursor()
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
                when (e.button) {
                    MouseEvent.BUTTON1 -> {
                        if (e.clickCount == 2) {
                            doZoomIn?.invoke(e.x, e.y)
                        }
                    }
                    MouseEvent.BUTTON3 -> {
                        doReRender?.invoke()
                    }
                    else -> {}
                }
            }

            override fun mousePressed(e: MouseEvent) {
                when (e.button) {
                    MouseEvent.BUTTON1 -> {
                        val x1 = e.x - rectangle.width / 2;
                        val y1 = e.y - rectangle.height / 2;
                        val x2 = rectangle.width + 1
                        val y2 = rectangle.height + 1
                        rectangle.setLocation(x1, y1)
                        frame.rectangle = rectangle
                        frame.repaint(x1, y1, x2, y1)
                        frame.repaint(x1, y1, x2, y2)
                        frame.repaint(x1, y2, x2, y2)
                        frame.repaint(x2, y1, x2, y2)
                        frame.cursor = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR)
                    }
                    else -> {}
                }
            }

            override fun mouseReleased(e: MouseEvent) {
                when (e.button) {
                    MouseEvent.BUTTON1 -> {
                        frame.rectangle = null
                        frame.cursor = Cursor.getDefaultCursor()
                        frame.repaint()
                    }
                    else -> {}
                }
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

        val rec = rectangle
        if (rec != null) {
            g2.color = Color.WHITE
            g2.draw(rec)
        }
    }
}



