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

        private val targetTime = Calendar.getInstance().apply {
            set(2026, Calendar.JANUARY, 17, 21, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

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
            handler.removeCallbacks(drawRunnable)
            if (visible) {
                draw()
            }
        }

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            handler.removeCallbacks(drawRunnable)
            draw()
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
            visible = false
            handler.removeCallbacks(drawRunnable)
        }

        private fun draw() {
            val holder = surfaceHolder
            var canvas: Canvas? = null
            
            val now = System.currentTimeMillis()
            val diff = targetTime - now

            try {
                canvas = holder.lockCanvas()
                if (canvas != null) {
                    // 1. Draw Background Parchment Color
                    canvas.drawColor(Color.parseColor("#e4d9c6"))

                    // 2. Draw Logo
                    val bitmap = BitmapFactory.decodeResource(resources, com.a7x.countdown.R.drawable.deathbat)
                    if (bitmap != null) {
                        val xOffset = if (diff <= 0) (Math.random() * 20 - 10).toFloat() else 0f
                        val yOffset = if (diff <= 0) (Math.random() * 20 - 10).toFloat() else 0f

                        val scale = Math.max(
                            canvas.width.toFloat() / bitmap.width.toFloat(),
                            canvas.height.toFloat() / bitmap.height.toFloat()
                        )
                        val dx = ((canvas.width - bitmap.width * scale) / 2f) + xOffset
                        val dy = ((canvas.height - bitmap.height * scale) / 2f) + yOffset
                        
                        val matrix = Matrix().apply {
                            postScale(scale, scale)
                            postTranslate(dx, dy)
                        }
                        canvas.drawBitmap(bitmap, matrix, Paint().apply { isFilterBitmap = true })
                    }

                    // 3. Countdown Display
                    val x = canvas.width / 2f
                    
                    if (diff > 0) {
                        val days = TimeUnit.MILLISECONDS.toDays(diff)
                        val hours = TimeUnit.MILLISECONDS.toHours(diff) % 24
                        val minutes = TimeUnit.MILLISECONDS.toMinutes(diff) % 60
                        val seconds = TimeUnit.MILLISECONDS.toSeconds(diff) % 60

                        val timeStr = String.format("%02d:%02d:%02d:%02d", days, hours, minutes, seconds)
                        val labelStr = "DIAS : HRS : MIN : SEG"
                        
                        paintText.textSize = 150f
                        paintText.color = Color.parseColor("#1a1a1a")
                        canvas.drawText(timeStr, x, canvas.height * 0.22f, paintText)
                        
                        paintSubText.textSize = 40f
                        canvas.drawText(labelStr, x, canvas.height * 0.26f, paintSubText)
                    } else {
                        paintText.textSize = 180f
                        paintText.color = Color.parseColor("#4a0000") // Red accent
                        canvas.drawText("¡YA ES EL SHOW!", x, canvas.height * 0.22f, paintText)
                    }
                    
                    // Band Info
                    canvas.drawText("AVENGED SEVENFOLD", x, canvas.height * 0.42f, paintBand)
                    
                    val paintAlbum = Paint(paintText).apply { 
                        textSize = 80f; 
                        letterSpacing = 0.05f; 
                        color = Color.parseColor("#1a1a1a") 
                    }
                    canvas.drawText("LIFE IS BUT A DREAM", x, canvas.height * 0.48f, paintAlbum)
                    
                    canvas.drawText("México 2026", x, canvas.height * 0.53f, paintSubText.apply { textSize = 55f })
                }
            } finally {
                canvas?.let { holder.unlockCanvasAndPost(it) }
            }
            
            if (visible) {
                val delay = if (diff <= 0) 50L else 1000L
                handler.removeCallbacks(drawRunnable)
                handler.postDelayed(drawRunnable, delay)
            }
        }

    }
}
