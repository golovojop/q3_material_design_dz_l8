/**
 * https://stackoverflow.com/questions/52771533/pause-progress-of-objectanimator/52783302
 *
 * https://medium.com/androiddevelopers/playing-with-paths-3fbc679a6f77
 * https://gist.github.com/nickbutcher/b41da75b8b1fc115171af86c63796c5b#file-polygonlapsdrawable-kt
 *
 * http://www.curious-creature.com/2013/12/21/android-recipe-4-path-tracing/?source=post_page-----3fbc679a6f77----------------------
 *https://github.com/romainguy/road-trip/blob/master/application/src/main/java/org/curiouscreature/android/roadtrip/IntroView.java
 */

package k.s.yarlykov.stylesapp

import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.drawable.Drawable
import android.util.Log
import android.util.Property
import androidx.core.content.ContextCompat

class CustomDrawable(private val radius : Float = 64f) : Drawable() {

    var progress = 1f
        set(value) {
            field = value.coerceIn(0f, 1f)
            callback?.invalidateDrawable(this)
        }

    private val cornerEffect = CornerPathEffect(8f)

    private val initialPhase = 0f

    private val linePaint = Paint(ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = 0xFF888888.toInt()
        strokeWidth = 4f
        pathEffect = cornerEffect
    }

    private val path = Path().apply {
        addCircle(cx, cy, radius, Path.Direction.CW)
    }

    private val length by lazy(LazyThreadSafetyMode.NONE) {
        pathMeasure.setPath(path, false)
        pathMeasure.length
    }

    override fun draw(canvas: Canvas) {
        if (progress < 1f) {

//            Log.e("APP_TAG", "progress=$progress")

//            val progressEffect = DashPathEffect(
//                floatArrayOf(0f, (1f - progress) * length, progress * length, 0f),
//                initialPhase)

            val progressEffect = DashPathEffect(
                floatArrayOf(4f, (1f - progress) * 14f, 4f, (1f - progress) * 14f),
                initialPhase)
            linePaint.pathEffect = ComposePathEffect(progressEffect, cornerEffect)

        }
        canvas.drawPath(path, linePaint)    }

    override fun setAlpha(alpha: Int) {
        linePaint.alpha = alpha
    }

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

    override fun setColorFilter(colorFilter: ColorFilter?) {
        linePaint.colorFilter = colorFilter
    }
    override fun getIntrinsicWidth() = width

    override fun getIntrinsicHeight() = height

    companion object {

        private const val width = 256
        private const val height = 256
        private const val cx = (width / 2).toFloat()
        private const val cy = (height / 2).toFloat()
        private val pathMeasure = PathMeasure()

        object PROGRESS : Property<CustomDrawable, Float>(Float::class.java, "progress") {
            override fun set(cd: CustomDrawable, value: Float) {
                cd.progress = value
            }
            override fun get(cd : CustomDrawable): Float = cd.progress
        }
    }
}