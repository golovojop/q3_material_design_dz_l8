package k.s.yarlykov.stylesapp

import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.Property

class FlyingJet(val width : Int, val height : Int, val marker : Bitmap, val radius: Float = 64f) : Drawable() {

    val cx = (width / 2).toFloat()
    val cy = (height / 2).toFloat()

    val cornerPathEffect = CornerPathEffect(24f)

    var progress = 1f
        set(value) {
            field = value.coerceIn(0f, 1f)
            callback?.invalidateDrawable(this)
        }

    var dotProgress = 0f
        set(value) {
            field = value.coerceIn(0f, 1f)
            callback?.invalidateDrawable(this)
        }

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = 0xCC888888.toInt()
        strokeWidth = 6f
        pathEffect = cornerPathEffect
    }

    private val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFFFFFFF.toInt()
        style = Paint.Style.FILL
    }

//    val pathCycle = Path().apply {
//        addCircle(cx, cy, radius, Path.Direction.CW)
//    }

    val pathCycle = Path().apply {
        moveTo(0f, (height/2).toFloat())
        lineTo(width.toFloat(), (height/2).toFloat())
    }

    val pathDot = Path().apply {
        addCircle(0f, 0f, 8f, Path.Direction.CW)
    }


    private val lengthPath by lazy(LazyThreadSafetyMode.NONE) {
        pathMeasure.setPath(pathCycle, false)
        pathMeasure.length
    }


    override fun draw(canvas: Canvas) {

        // Отрисовать траекторию
        canvas.drawPath(pathCycle, linePaint)

        // Отрисовать маркер

        // Координаты маркера в текущей позиции на пути
        val pos = floatArrayOf(0f, 0f)
        // Угол катательной в текущей позиции на пути
        val tan = floatArrayOf(0f, 0f)

        pathMeasure.getPosTan(dotProgress * lengthPath, pos, tan)
        canvas.translate(pos[0], pos[1])
        val angle = Math.atan2(tan[1].toDouble(), tan[0].toDouble())

        canvas.rotate(Math.toDegrees(angle).toFloat())

        val bitmap : Bitmap = marker
        val rect: RectF = RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())
        canvas.drawBitmap(bitmap, null, rect, linePaint )

//        val advance = lengthPath
//        val phase = dotProgress * lengthPath
//
//        dotPaint.pathEffect =
//            ComposePathEffect(
//                PathDashPathEffect(pathDot, advance, phase, PathDashPathEffect.Style.TRANSLATE),
//                cornerPathEffect
//            )
//        canvas.drawPath(pathCarrier, dotPaint)
    }

    /**
     * Переопределенные функции класса Drawable
     */
    override fun getIntrinsicHeight(): Int  = height
    override fun getIntrinsicWidth(): Int = width
    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
    override fun setColorFilter(colorFilter: ColorFilter?) {
        linePaint.colorFilter = colorFilter
    }
    override fun setAlpha(alpha: Int) {
        linePaint.alpha = alpha
    }

    companion object {
        private val pathMeasure = PathMeasure()

        object PROGRESS : Property<FlyingJet, Float>(Float::class.java, "progress") {
            override fun set(fj: FlyingJet, value: Float) {
                fj.progress = value
            }

            override fun get(fj: FlyingJet): Float = fj.progress
        }

        object DOT_PROGRESS : Property<FlyingJet, Float>(Float::class.java, "dotProgress") {
            override fun set(fj: FlyingJet, value: Float) {
                fj.dotProgress = value
            }

            override fun get(fj: FlyingJet): Float = fj.dotProgress
        }
    }
}