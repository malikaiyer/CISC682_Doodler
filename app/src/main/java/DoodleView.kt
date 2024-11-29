package com.example.cisc682_doodler

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class DoodleView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {
    // Track individual strokes
    private val strokes = mutableListOf<Stroke>()

    // Current brush configuration
    private var currentColor = Color.BLACK
    private var currentSize = 5f
    private var currentOpacity = 255

    // Data class to track each individual stroke
    private data class Stroke(
        val path: Path,
        val paint: Paint
    )

    // Current path being drawn
    private var currentPath = Path()
    var currentPaint = Paint().apply {
        color = currentColor
        strokeWidth = currentSize
        alpha = currentOpacity
        isAntiAlias = true
        style = Paint.Style.STROKE
    }

    fun updateBrush(color: Int, size: Float, opacity: Int) {
        currentColor = color
        currentSize = size
        currentOpacity = opacity
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // Create a new path and paint for this stroke
                currentPath = Path()
                currentPaint = Paint().apply {
                    color = currentColor
                    strokeWidth = currentSize
                    alpha = currentOpacity
                    isAntiAlias = true
                    style = Paint.Style.STROKE
                }
                currentPath.moveTo(event.x, event.y)
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                currentPath.lineTo(event.x, event.y)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                // Save the completed stroke
                strokes.add(Stroke(currentPath, currentPaint))
            }
        }
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Redraw all previous strokes
        strokes.forEach { stroke ->
            canvas.drawPath(stroke.path, stroke.paint)
        }
        // Draw the current path
        canvas.drawPath(currentPath, currentPaint)
    }

    fun clearCanvas() {
        strokes.clear()
        currentPath.reset()
        invalidate()
    }
}