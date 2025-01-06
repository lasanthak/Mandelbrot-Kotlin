@file:Suppress("unused")

package com.lasantha.fractal.render

import com.lasantha.fractal.calc.PointResult
import com.lasantha.fractal.matrix.Matrix
import com.lasantha.fractal.render.color.ColorCoder
import java.awt.*
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO
import javax.swing.*

class JFrameRenderer(
        override val width: Int,
        override val height: Int,
        override val titleText: String
) : Renderer<PointResult<Double>> {

    private val imageDimension = Dimension(width, height)
    private val windowDimension = Toolkit.getDefaultToolkit().screenSize
    private val frame = FractalFrame(imageDimension, windowDimension, titleText)
    private val rectangle = Rectangle(0, 0, windowDimension.width / 4, windowDimension.height / 4)
    private var doZoomIn: ((x: Int, y: Int, w: Int, h: Int) -> Unit)? = null
    private var doReRender: (() -> Unit)? = null

    override fun render(matrix: Matrix<*, PointResult<Double>>, colorCoder: ColorCoder) {
        val image = frame.panel.image
        if (matrix.width > width || matrix.height > height) {
            throw IllegalArgumentException("Invalid dimensions")
        }
        matrix.forEach { result, x, y -> image.setRGB(x, y, colorCoder.toRGB(result)) }
        frame.repaint()
    }

    override fun zoomInHandler(doZoomIn: (x: Int, y: Int, w: Int, h: Int) -> Unit) {
        this.doZoomIn = doZoomIn
    }

    override fun reRenderHandler(doReRender: () -> Unit) {
        this.doReRender = doReRender
    }

    override fun indicateBusy(busy: Boolean) {
        frame.cursor = if (busy) Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) else Cursor.getDefaultCursor()
    }

    private fun saveImage() {
        val image = frame.panel.image
        val fileName = "${System.currentTimeMillis()}.png"
        try {
            ImageIO.write(image, "png", File(fileName))
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

        frame.panel.addMouseListener(object : MouseListener {
            override fun mouseClicked(e: MouseEvent) {
                when (e.button) {
                    MouseEvent.BUTTON1 -> {
                        if (e.clickCount == 2) {
                            doZoomIn?.invoke(e.x, e.y, rectangle.width, rectangle.height)
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
                        val x = e.x - rectangle.width / 2
                        val y = e.y - rectangle.height / 2
                        rectangle.setLocation(x, y)
                        frame.panel.rectangle = rectangle
                        repaintRectangleArea(rectangle)
                        frame.cursor = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR)
                    }
                    else -> {}
                }
            }

            override fun mouseReleased(e: MouseEvent) {
                when (e.button) {
                    MouseEvent.BUTTON1 -> {
                        frame.panel.rectangle = null
                        repaintRectangleArea(rectangle)
                        frame.cursor = Cursor.getDefaultCursor()
                    }
                    else -> {}
                }
            }

            private fun repaintRectangleArea(rectangle: Rectangle) {
                val x1 = rectangle.x
                val y1 = rectangle.y
                val x2 = rectangle.width + 1
                val y2 = rectangle.height + 1
                frame.panel.repaint(x1, y1, x2, y1)
                frame.panel.repaint(x1, y1, x2, y2)
                frame.panel.repaint(x1, y2, x2, y2)
                frame.panel.repaint(x2, y1, x2, y2)
            }

            override fun mouseEntered(e: MouseEvent) {}
            override fun mouseExited(e: MouseEvent) {}
        })
    }
}

private class FractalFrame(imageDimension: Dimension,
                           windowDimension: Dimension,
                           titleText: String) : JFrame(titleText) {
    val panel = FractalPanel(imageDimension)
    val scroller = JScrollPane(panel)

    init {
        this.defaultCloseOperation = EXIT_ON_CLOSE
        this.setSize(windowDimension.width, windowDimension.height)
        this.add(scroller)
        this.isVisible = true
        this.requestFocus()
    }
}

private class FractalPanel(imageDimension: Dimension) : JPanel(false) {
    val image = BufferedImage(imageDimension.width, imageDimension.height, BufferedImage.TYPE_INT_RGB)
    var rectangle: Rectangle? = null

    init {
        this.preferredSize = imageDimension
        this.revalidate()
    }

    override fun paintComponent(g: Graphics?) {
        super.paintComponent(g)
        val g2 = g as Graphics2D
        g2.drawImage(image, null, 0, 0)

        val rec = rectangle
        if (rec != null) {
            g2.color = Color.WHITE
            g2.draw(rec)
        }
    }
}



