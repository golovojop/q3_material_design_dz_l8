package k.s.yarlykov.stylesapp

import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.Property
import k.s.yarlykov.stylesapp.graphics.PathParser

class CityContour(val width: Int, val height: Int) : Drawable() {

    var contourProgress = 0f
        set(value) {
            field = value.coerceIn(0f, 1f)
            callback?.invalidateDrawable(this)
        }

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = 0x88FFFFFF.toInt()
        strokeWidth = 3f
    }

    // pathData from @drawable\ic_building
    private val pathContour = PathParser.createPathFromPathData(pathData)

    private val lengthContourPath by lazy(LazyThreadSafetyMode.NONE) {
        pathMeasure.setPath(pathContour, false)
        pathMeasure.length
    }

    private fun dashEffectDrawing(): DashPathEffect =
        DashPathEffect(
            floatArrayOf(0f, (1f - contourProgress) * lengthContourPath, contourProgress * lengthContourPath, 0f),
            initialPhase
        )

    override fun draw(canvas: Canvas) {
        if (contourProgress < 1f) {
            linePaint.pathEffect = dashEffectDrawing()
        }
        canvas.drawPath(pathContour, linePaint)
    }

    override fun getIntrinsicHeight(): Int = height
    override fun getIntrinsicWidth(): Int = width
    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
    override fun setAlpha(alpha: Int) {
        linePaint.alpha = alpha
    }
    override fun setColorFilter(colorFilter: ColorFilter?) {
        linePaint.colorFilter = colorFilter
    }

    private fun pathLineDebug() =
        Path().also {p ->
            p.moveTo(0f, 0f)
            p.lineTo(164f, 164f)
        }

    companion object {
        private val pathMeasure = PathMeasure()

        private val initialPhase = 0f

        private val pathData = "M97.59,39.78 L80.81,21.93a1.5,1.5 0,0 0,-1.08 -0.47,1.69 1.69,0 0,0 -1.09,0.45L64,37V9.5A1.5,1.5 0,0 0,62.5 8h-34A1.5,1.5 0,0 0,27 9.5V30H5.5A1.5,1.5 0,0 0,4 31.5v33a1.5,1.5 0,0 0,3 0V33H27V53.5a1.5,1.5 0,0 0,3 0V11H61V85H20.5a1.5,1.5 0,0 0,0 3h60a1.5,1.5 0,0 0,0 -3H64V41.25L79.7,25.13 95,41.4V62.5a1.5,1.5 0,0 0,3 0V40.8A1.5,1.5 0,0 0,97.59 39.78Z"

        object CONTOUR_PROGRESS : Property<CityContour, Float>(Float::class.java, "contourProgress") {
            override fun set(cc: CityContour, value: Float) {
                cc.contourProgress = value
            }

            override fun get(cc: CityContour): Float = cc.contourProgress
        }
    }
}