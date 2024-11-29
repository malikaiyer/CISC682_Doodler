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
    val paint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 5f
        isAntiAlias = true
        style = Paint.Style.STROKE
    }
    private val path = Path()

    private var brushColor = Color.BLACK
    private var brushSize = 5f
    private var brushOpacity = 255


    init {
        paint.color = brushColor
        paint.strokeWidth = brushSize
        paint.alpha = brushOpacity
    }

    constructor(context: Context) : this(context, null)


    // Set color, brush size, and opacity
    fun updateBrush(color: Int, size: Float, opacity: Int) {
        paint.color = color
        paint.strokeWidth = size
        paint.alpha = opacity
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                path.moveTo(event.x, event.y)
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                path.lineTo(event.x, event.y)
                invalidate()
            }
            MotionEvent.ACTION_UP -> { }
        }
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(path, paint)
    }

    fun clearCanvas() {
        path.reset()
        invalidate()
    }
}
