/**
 * https://stackoverflow.com/questions/52771533/pause-progress-of-objectanimator/52783302
 */

package k.s.yarlykov.stylesapp

import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.drawable.Drawable
import android.util.Property
import androidx.core.content.ContextCompat

class CustomDrawable(val radius : Float) : Drawable() {

    var progress = 1f
        set(value) {
            field = value.coerceIn(0f, 1f)
            callback.invalidateDrawable(this)
        }

    private val cornerEffect = CornerPathEffect(8f)

    private val initialPhase = 4f

    private val length = 2 * Math.PI.toFloat() * radius

    private val linePaint = Paint(ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = 0xFF888888.toInt()
        strokeWidth = 4f
        pathEffect = cornerEffect
    }

    val path = Path().apply {
        addCircle(450f, 150f, 125f, Path.Direction.CW)
    }

    override fun draw(canvas: Canvas) {
        if (progress < 1f) {
            val progressEffect = DashPathEffect(
                floatArrayOf(0f, (1f - progress) * length, progress * length, 0f),
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


        val PROGRESS = Property.of(CustomDrawable::class.java, Float::class.java, "progress")
    }
}