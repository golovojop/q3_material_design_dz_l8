/**
 * https://stackoverflow.com/questions/52771533/pause-progress-of-objectanimator/52783302
 *
 * https://medium.com/androiddevelopers/playing-with-paths-3fbc679a6f77
 * https://gist.github.com/nickbutcher/b41da75b8b1fc115171af86c63796c5b#file-polygonlapsdrawable-kt
 *
 * http://www.curious-creature.com/2013/12/21/android-recipe-4-path-tracing/?source=post_page-----3fbc679a6f77----------------------
 * https://github.com/romainguy/road-trip/blob/master/application/src/main/java/org/curiouscreature/android/roadtrip/IntroView.java
 *
 * How Android draws
 * https://developer.android.com/guide/topics/ui/how-android-draws
 */

package k.s.yarlykov.stylesapp

import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.drawable.Drawable
import android.util.Property
import k.s.yarlykov.stylesapp.graphics.getUfoShapePath
import k.s.yarlykov.stylesapp.graphics.makeConvexArrow
import k.s.yarlykov.stylesapp.graphics.makeTakeOffPath
import k.s.yarlykov.stylesapp.graphics.makeTakeOffPathShifted

class CustomDrawable(private val radius: Float = 64f) : Drawable() {

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

    private val cornerEffect = CornerPathEffect(24f)

    private val linePaint = Paint(ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = 0xCC888888.toInt()
        strokeWidth = 6f
        pathEffect = cornerEffect
    }

    private val dotPaint = Paint(ANTI_ALIAS_FLAG).apply {
        color = 0xFFFFFFFF.toInt()
        style = Paint.Style.FILL
    }

    val pathCycle = Path().apply {
        addCircle(cx, cy, radius, Path.Direction.CW)
    }

    val pathTrack = Path().apply {
        addCircle(cx, cy, radius + 8, Path.Direction.CW)
    }

    val pathDot = Path().apply {
        addCircle(0f, 0f, 8f, Path.Direction.CW)
    }

    val pathConvexArrow = makeConvexArrow(32f, 12f)

    val pathUfo = getUfoShapePath()

    val pathTakeOff = makeTakeOffPath(width.toFloat(), height.toFloat())
    val pathTakeOffShifted: Path =
        makeTakeOffPathShifted(width.toFloat(), height.toFloat(), 20f)

    private val lengthCycle by lazy(LazyThreadSafetyMode.NONE) {
        pathMeasure.setPath(pathCycle, false)
        pathMeasure.length
    }

    private val lengthTrack by lazy(LazyThreadSafetyMode.NONE) {
        pathMeasure.setPath(pathTrack, false)
        pathMeasure.length
    }

    private val lengthTakeOff by lazy(LazyThreadSafetyMode.NONE) {
        pathMeasure.setPath(pathTakeOff, false)
        pathMeasure.length
    }


    private val initialPhase = lengthCycle * 0.05f

    private val pathArrow = makeConvexArrow(32f, 12f)

    override fun draw(canvas: Canvas) {
        if (progress < 1f) {
            val progressEffect = dashEffectCircleDrawing()
            linePaint.pathEffect = ComposePathEffect(progressEffect, cornerEffect)
        }
//        canvas.drawPath(pathCycle, linePaint)

        canvas.drawPath(pathTakeOff, linePaint)
        canvas.drawPath(pathTakeOffShifted, linePaint)

        val advance = lengthTakeOff
        val phase = initialPhase - dotProgress * lengthTakeOff

        dotPaint.pathEffect =
            ComposePathEffect(
                PathDashPathEffect(pathUfo, advance, phase, PathDashPathEffect.Style.ROTATE),
                cornerEffect
            )
        canvas.drawPath(pathTakeOff, dotPaint)
    }

    /**
     * floatArrayOf(...) определяет вид пуктирной линии, которая будет рисовать path,
     * а именно это набор из "тире" и "пробелов", точнее из длин (в пикселях) "тире" и "пробелов".
     * Например floatArrayOf(5f, 10f, 20f, 30f) говорит, что сначала идет тире длиной 5,
     * потом пробел длиной 10, потом снова тире длиной 20 и пробел длиной 30. Потом все циклино
     * повторится по всей длине path. В указанном примере получится статическая картинка. Однако
     * если реагировать на progress, то можно анимировать процесс отрисовки.
     */
    private fun dashEffectDashedDrawing(dash: Float = 16f): DashPathEffect =
        DashPathEffect(
            floatArrayOf(dash, (1f - progress) * dash, dash, (1f - progress) * dash),
            initialPhase
        )

    /**
     * В этом алгоритме мы устанавливаем исходное состояние на момент начала
     * рисования таким образом, что длина тире нулевая, а длина пробела -
     * вся длина нашего path. По мере изменения progress от 0.00 до 1.00
     * у нас будет уменьшаться длина пробела в первой паре, и увеличиваться
     * длина тире во второй паре аргументов floatArrayOf. А значит мы будем
     * увидим как последовательно отрисовываться линия окружности.
     */
    private fun dashEffectCircleDrawing(): DashPathEffect =
        DashPathEffect(
            floatArrayOf(0f, (1f - progress) * lengthCycle, progress * lengthCycle, 0f),
            initialPhase
        )

    /**
     * Переопределенные функции класса Drawable
     */
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
        private const val width = 600
        private const val height = 400
        private const val cx = (width / 2).toFloat()
        private const val cy = (height / 2).toFloat()
        private val pathMeasure = PathMeasure()

        object PROGRESS : Property<CustomDrawable, Float>(Float::class.java, "progress") {
            override fun set(cd: CustomDrawable, value: Float) {
                cd.progress = value
            }

            override fun get(cd: CustomDrawable): Float = cd.progress
        }

        object DOT_PROGRESS : Property<CustomDrawable, Float>(Float::class.java, "dotProgress") {
            override fun set(cd: CustomDrawable, value: Float) {
                cd.dotProgress = value
            }

            override fun get(cd: CustomDrawable): Float = cd.dotProgress
        }

    }
}