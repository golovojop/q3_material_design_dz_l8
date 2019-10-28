package k.s.yarlykov.stylesapp

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import android.util.Property
import androidx.core.content.ContextCompat
import k.s.yarlykov.stylesapp.graphics.cityOutlinedPath

enum class AnimationMode {
    FORWARD, BACK
}

class CityContour(contourData: String, contourWidth: Float, val screen: Pair<Int, Int>) : Drawable() {

    var contourProgress = 0f
        set(value) {
            field = value.coerceIn(0f, 1f)
            callback?.invalidateDrawable(this)
        }

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = 0x88FFFFFF.toInt()
        strokeWidth = 5f
    }

    lateinit var mode : AnimationMode


    private val pathContour = cityOutlinedPath(contourData, contourWidth, screen)

    private val lengthContourPath by lazy(LazyThreadSafetyMode.NONE) {
        pathMeasure.setPath(pathContour, false)
        pathMeasure.length
    }

    private fun dashEffectDrawing(): DashPathEffect =
        DashPathEffect(
            floatArrayOf(contourProgress * lengthContourPath, (1f - contourProgress) * lengthContourPath),
            initialPhase
        )

    override fun draw(canvas: Canvas) {
        if (contourProgress < 1f) {
            linePaint.pathEffect = dashEffectDrawing()
        }
        canvas.drawPath(pathContour, linePaint)
    }

    override fun getIntrinsicWidth(): Int = screen.first
    override fun getIntrinsicHeight(): Int = screen.second / 6
    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
    override fun setAlpha(alpha: Int) {
        linePaint.alpha = alpha
    }
    override fun setColorFilter(colorFilter: ColorFilter?) {
        linePaint.colorFilter = colorFilter
    }

    companion object {
        private val pathMeasure = PathMeasure()

        private var initialPhase = 0f

        object CONTOUR_PROGRESS : Property<CityContour, Float>(Float::class.java, "contourProgress") {
            override fun set(cc: CityContour, value: Float) {
                cc.contourProgress = value
            }

            override fun get(cc: CityContour): Float = cc.contourProgress
        }
    }
}