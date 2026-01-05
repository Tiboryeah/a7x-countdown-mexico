package com.a7x.countdown

import android.graphics.*
import android.os.Handler
import android.os.Looper
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import java.util.*
import java.util.concurrent.TimeUnit

class A7XWallpaperService : WallpaperService() {
    override fun onCreateEngine(): Engine = WallpaperEngine()

    inner class WallpaperEngine : Engine() {
        private val handler = Handler(Looper.getMainLooper())
        private val drawRunnable = Runnable { draw() }
        private var visible = false

        private val paintText = Paint().apply {
            color = Color.BLACK
            textSize = 200f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        private val paintSubText = Paint().apply {
            color = Color.BLACK
            textSize = 60f
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        override fun onVisibilityChanged(visible: Boolean) {
            this.visible = visible
            if (visible) draw() else handler.removeCallbacks(drawRunnable)
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
            visible = false
            handler.removeCallbacks(drawRunnable)
        }

        private fun draw() {
            val holder = surfaceHolder
            var canvas: Canvas? = null
            try {
                canvas = holder.lockCanvas()
                if (canvas != null) {
                    // 1. Draw Background Parchment Color
                    canvas.drawColor(Color.parseColor("#d8cebc"))

                    // 2. Draw Logo
                    val bitmap = BitmapFactory.decodeResource(resources, com.a7x.countdown.R.drawable.deathbat)
                    if (bitmap != null) {
                        val scale = canvas.width.toFloat() / bitmap.width.toFloat()
                        val matrix = Matrix().apply {
                            postScale(scale, scale)
                            postTranslate(0f, (canvas.height - bitmap.height * scale) / 2f + 200f) // Push it down
                        }
                        canvas.drawBitmap(bitmap, matrix, null)
                    }

                    // 3. Calculate Days
                    val days = getDaysRemaining()
                    
                    // 4. Draw Text
                    val x = canvas.width / 2f
                    canvas.drawText("$days", x, canvas.height * 0.2f, paintText)
                    canvas.drawText("DIAS", x, canvas.height * 0.28f, paintSubText)
                    
                    canvas.drawText("AVENGED SEVENFOLD", x, canvas.height * 0.45f, paintSubText)
                    canvas.drawText("LIFE IS BUT A DREAM", x, canvas.height * 0.52f, paintText.apply { textSize = 80f })
                }
            } finally {
                canvas?.let { holder.unlockCanvasAndPost(it) }
            }
            
            if (visible) {
                handler.postDelayed(drawRunnable, 60000) // Update every minute
            }
        }

        private fun getDaysRemaining(): Long {
            val target = Calendar.getInstance().apply {
                set(2026, Calendar.JANUARY, 17, 0, 0, 0)
            }.timeInMillis
            val diff = target - System.currentTimeMillis()
            return if (diff > 0) TimeUnit.MILLISECONDS.toDays(diff) + 1 else 0
        }
    }
}
