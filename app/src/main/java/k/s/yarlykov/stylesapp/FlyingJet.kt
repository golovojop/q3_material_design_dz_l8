package k.s.yarlykov.stylesapp

import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.Property
import k.s.yarlykov.stylesapp.graphics.pathBezier

class FlyingJet(val width: Int, val height: Int, private val marker: Bitmap, private val radius: Float = 64f) :
    Drawable() {

    private val cx = (width / 2).toFloat()
    private val cy = (height / 2).toFloat()

    private val cornerPathEffect = CornerPathEffect(24f)

    private var progress = 1f
        set(value) {
            field = value.coerceIn(0f, 1f)
            callback?.invalidateDrawable(this)
        }

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = 0xFFFFFFFF.toInt()
        strokeWidth = 3f
        pathEffect = cornerPathEffect
    }

//    val pathCarrier = Path().apply {
//        addCircle(cx, cy, radius, Path.Direction.CW)
//    }

    // "Опорный Path по которому будет двигаться все остальное"
    val pathCarrier = pathBezier()

    private val lengthCarrierPath by lazy(LazyThreadSafetyMode.NONE) {
        pathMeasure.setPath(pathCarrier, false)
        pathMeasure.length
    }

//    private fun dashEffectCircleDrawing(): DashPathEffect =
//        DashPathEffect(
//            floatArrayOf(0f, lengthPath, progress * lengthPath, 0f),
//            lengthPath
//        )


    private fun dashEffectDrawing(): DashPathEffect =
        DashPathEffect(
            floatArrayOf(8f, 8f, 8f, 8f),
            lengthCarrierPath
        )

    override fun draw(canvas: Canvas) {

        val partialPath = Path()
        pathMeasure.getSegment(0f, progress * lengthCarrierPath, partialPath, true)

        // Отрисовать траекторию
        if (progress < 1f) {
            val progressEffect = dashEffectDrawing()
            linePaint.pathEffect = progressEffect
        }
        canvas.drawPath(partialPath, linePaint)

        // Отрисовать маркер
        // Координаты маркера в текущей позиции на пути
        val pos = floatArrayOf(0f, 0f)
        // Угол катательной в текущей позиции на пути
        val tan = floatArrayOf(0f, 0f)

        // Настравиваем трансформации канвы. Дело в том, что при каждом "тике" нам нужно
        // отрисовать один и тот же маркер, но в на другой позиции и под дгуим узлом.
        // Соответственно мы и говорим канве в какой позиции она должна сейчас рисовать
        // и под каким углом рисовать.
        pathMeasure.getPosTan(progress * lengthCarrierPath, pos, tan)
        canvas.translate(pos[0], pos[1])
        val angle = Math.atan2(tan[1].toDouble(), tan[0].toDouble())
        canvas.rotate(Math.toDegrees(angle).toFloat())

        // Важный момент. Когда рисуем маркер по Path, то он не центрируется по линии этого Path,
        // а органичивается по ней одной из своих сторон (зависит от текущего положения маркера).
        //  Чтобы отцентрироваться я сдвигаю прямоугольник маркера вверх на середину его высоты.
        val rect = RectF(0f, 0f, marker.width.toFloat(), marker.height.toFloat())
        rect.offset(0f, -marker.height.toFloat() / 2)

        canvas.drawBitmap(marker, null, rect, linePaint)
    }

    /**
     * Переопределенные функции класса Drawable
     */
    override fun getIntrinsicHeight(): Int = height

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
    }
}