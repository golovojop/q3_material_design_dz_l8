package k.s.yarlykov.stylesapp

import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.Property
import k.s.yarlykov.stylesapp.graphics.cityOutlinedPath

enum class AnimationMode {
    APPEAR, DISAPPEAR
}

class CityContour(
    contourData: String,
    contourWidth: Float,
    private val screen: Pair<Int, Int>
) : Drawable() {

    var contourProgress = 0f
        set(value) {
            field = value.coerceIn(0f, 1f)
            callback?.invalidateDrawable(this)
        }

    var animationMode: AnimationMode = AnimationMode.APPEAR

    private val dashLength = 150F

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = 0x88FFFFFF.toInt()
        strokeWidth = 5f
    }

    private val pathContour = cityOutlinedPath(contourData, contourWidth, screen)

    private val lengthContourPath by lazy(LazyThreadSafetyMode.NONE) {
        pathMeasure.setPath(pathContour, false)
        pathMeasure.length
    }

    private fun dashEffectDrawing(): DashPathEffect =
        DashPathEffect(
            floatArrayOf(
                contourProgress * lengthContourPath,
                (1f - contourProgress) * lengthContourPath
            ),
            initialPhase
        )

    private fun disappearDashEffectDrawing(): DashPathEffect =
        DashPathEffect(
            floatArrayOf(
                (1f - contourProgress) * dashLength,
                contourProgress * dashLength
            ),
            initialPhase
        )

    override fun draw(canvas: Canvas) {
        if (contourProgress < 1f) {
            linePaint.pathEffect =
                if (animationMode == AnimationMode.APPEAR)
                    dashEffectDrawing()
                else
                    disappearDashEffectDrawing()
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

        object CONTOUR_PROGRESS :
            Property<CityContour, Float>(Float::class.java, "contourProgress") {
            override fun set(cc: CityContour, value: Float) {
                cc.contourProgress = value
            }

            override fun get(cc: CityContour): Float = cc.contourProgress
        }
    }
}