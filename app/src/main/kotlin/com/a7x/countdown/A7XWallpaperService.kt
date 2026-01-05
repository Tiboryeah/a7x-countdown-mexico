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
            color = Color.parseColor("#1a1a1a")
            textSize = 300f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
            setShadowLayer(10f, 0f, 0f, Color.parseColor("#44000000"))
        }

        private val paintSubText = Paint().apply {
            color = Color.parseColor("#2a2a2a")
            textSize = 80f
            typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        private val paintBand = Paint().apply {
            color = Color.BLACK
            textSize = 50f
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
            letterSpacing = 0.2f
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
                    // 1. Draw Background Parchment Color (matches the image better)
                    canvas.drawColor(Color.parseColor("#e4d9c6"))

                    // 2. Draw Logo - Scaled to cover or fit nicely
                    val bitmap = BitmapFactory.decodeResource(resources, com.a7x.countdown.R.drawable.deathbat)
                    if (bitmap != null) {
                        // "Cover" logic
                        val scale = Math.max(
                            canvas.width.toFloat() / bitmap.width.toFloat(),
                            canvas.height.toFloat() / bitmap.height.toFloat()
                        )
                        val dx = (canvas.width - bitmap.width * scale) / 2f
                        val dy = (canvas.height - bitmap.height * scale) / 2f
                        
                        val matrix = Matrix().apply {
                            postScale(scale, scale)
                            postTranslate(dx, dy)
                        }
                        canvas.drawBitmap(bitmap, matrix, Paint().apply { isFilterBitmap = true })
                    }

                    // 3. Calculate Days
                    val days = getDaysRemaining()
                    
                    // 4. Draw Text with better hierarchy
                    val x = canvas.width / 2f
                    
                    // Countdown
                    canvas.drawText("$days", x, canvas.height * 0.22f, paintText)
                    canvas.drawText("DIAS", x, canvas.height * 0.28f, paintSubText)
                    
                    // Band Info (positioned to not overlap the skull too much)
                    canvas.drawText("AVENGED SEVENFOLD", x, canvas.height * 0.42f, paintBand)
                    
                    val paintAlbum = Paint(paintText).apply { textSize = 90f; letterSpacing = 0.05f }
                    canvas.drawText("LIFE IS BUT A DREAM", x, canvas.height * 0.48f, paintAlbum)
                    
                    canvas.drawText("México 2026", x, canvas.height * 0.53f, paintSubText.apply { textSize = 55f })
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
