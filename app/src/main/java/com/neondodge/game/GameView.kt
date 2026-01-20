package com.neondodge.game

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import kotlin.random.Random

class GameView(context: Context) : SurfaceView(context), Runnable {

    private val thread = Thread(this)
    private var running = false
    private val holderRef: SurfaceHolder = holder

    private val paint = Paint()

    // Player
    private var playerX = 300f
    private val playerY = 900f
    private val playerRadius = 50f

    // Obstacle
    private var obsX = 0f
    private var obsY = -100f
    private val obsRadius = 40f
    private var speed = 8f

    private var score = 0

    fun start() {
        running = true
        thread.start()
    }

    fun stop() {
        running = false
    }

    override fun run() {
        while (running) {
            if (!holderRef.surface.isValid) continue
            val canvas = holderRef.lockCanvas()
            update()
            drawGame(canvas)
            holderRef.unlockCanvasAndPost(canvas)
            try {
                Thread.sleep(16) // ~60 FPS
            } catch (_: Exception) {}
        }
    }

    private fun update() {
        obsY += speed
        if (obsY > height) {
            obsY = -100f
            obsX = Random.nextInt(100, width - 100).toFloat()
            score++
        }

        // Collision simple
        val dx = playerX - obsX
        val dy = playerY - obsY
        val dist = kotlin.math.sqrt(dx * dx + dy * dy)
        if (dist < playerRadius + obsRadius) {
            // Game Over → reset
            obsY = -100f
            score = 0
        }
    }

    private fun drawGame(canvas: Canvas) {
        canvas.drawColor(Color.BLACK)

        // Player (bleu néon)
        paint.color = Color.CYAN
        canvas.drawCircle(playerX, playerY, playerRadius, paint)

        // Obstacle (rose néon)
        paint.color = Color.MAGENTA
        canvas.drawCircle(obsX, obsY, obsRadius, paint)

        // Score
        paint.color = Color.WHITE
        paint.textSize = 48f
        canvas.drawText("Score: $score", 40f, 80f, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_MOVE ||
            event.action == MotionEvent.ACTION_DOWN) {
            playerX = event.x
        }
        return true
    }
}
