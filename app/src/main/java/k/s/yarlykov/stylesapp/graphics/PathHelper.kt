package k.s.yarlykov.stylesapp.graphics

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Path

/**
 * Функция строит Path для фигурки стрелки, направленной вправо.
 */
fun makeConvexArrow(length: Float, height: Float): Path {
    val p = Path()
    p.moveTo(0.0f, -height / 2.0f)
    p.lineTo(length - height / 4.0f, -height / 2.0f)
    p.lineTo(length, 0.0f)
    p.lineTo(length - height / 4.0f, height / 2.0f)
    p.lineTo(0.0f, height / 2.0f)
    p.lineTo(0.0f + height / 4.0f, 0.0f)
    p.close()
    return p
}

fun getUfoShapePath(): Path {
    val icFlight =
        "M21,16v-2l-8,-5V3.5c0,-0.83 -0.67,-1.5 -1.5,-1.5S10,2.67 10,3.5V9l-8,5v2l8,-2.5V19l-2,1.5V22l3.5,-1 3.5,1v-1.5L13,19v-5.5l8,2.5z"
    val icSend = "M2.01,21L23,12 2.01,3 2,10l15,2 -15,2z"
    return PathParser.createPathFromPathData(icSend)
}

fun makeTakeOffPath(width: Float, height: Float): Path {
    val p = Path()
    p.moveTo(0.0f, height / 4.0f)              // Point 1
    p.lineTo(width - width / 4.0f, height / 4.0f)               // Point 2
    p.lineTo(width - width / 4.0f, height - height / 4.0f)             // Point 3
    p.lineTo(width / 4.0f, height - height / 4.0f)             // Point 4
    p.lineTo(width / 4.0f, height / 4.0f + height / 8.0f)              // Point 5
    p.lineTo(width, height / 4.0f + height / 8.0f)
    return p
}

fun makeTakeOffPathShifted(width: Float, height: Float, delta: Float): Path {
    val p = Path()
    p.moveTo(0.0f, height / 4.0f + delta)              // Point 1
    p.lineTo(width - width / 4.0f - delta, height / 4.0f + delta)               // Point 2
    p.lineTo(width - width / 4.0f - delta, height - height / 4.0f - delta)             // Point 3
    p.lineTo(width / 4.0f + delta, height - height / 4.0f - delta)             // Point 4
    p.lineTo(width / 4.0f + delta, height / 4.0f + height / 8.0f + delta)              // Point 5
    p.lineTo(width, height / 4.0f + height / 8.0f + delta)
    return p
}

fun rotateBitmap(bitmap: Bitmap, angle: Float): Bitmap {

    return with(Matrix()) {
        postRotate(angle)
        Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, this, true)
    }
}

/**
 * https://stackoverflow.com/questions/22763632/construct-spline-with-android-graphics-path
 * https://ovpwp.wordpress.com/2008/12/17/how-to-draw-a-smooth-curve-through-a-set-of-2d-points-with-bezier-methods/
 */
fun pathBezier(): Path {

//    val knotsArr = listOf(
//        BezierSplineUtil.Point(0f, 100f),
//        BezierSplineUtil.Point(155f, 255f),
//        BezierSplineUtil.Point(400f, 300f),
//        BezierSplineUtil.Point(15f, 5f)
//    )

    val knotsArr = listOf(
        BezierSplineUtil.Point(-100f, 200f),
        BezierSplineUtil.Point(300f, 300f),

        BezierSplineUtil.Point(450f, 170f),
        BezierSplineUtil.Point(300f, 80f),

        BezierSplineUtil.Point(180f, 200f),
        BezierSplineUtil.Point(300f, 370f),

        BezierSplineUtil.Point(450f, 350f),
        BezierSplineUtil.Point(580f, 220f),

        BezierSplineUtil.Point(500f, 50f),
        BezierSplineUtil.Point(300f, 60f),

        BezierSplineUtil.Point(150f, 90f),
        BezierSplineUtil.Point(-110f, 200f)
    )

    val controlPoints = BezierSplineUtil.getCurveControlPoints(knotsArr.toTypedArray())

    val firstCP = controlPoints[0]
    val secondCP = controlPoints[1]

    val p = Path()
    p.moveTo(knotsArr[0].x, knotsArr[0].y)

    for (i in 0 until firstCP.size) {
        p.cubicTo(
            firstCP[i].x, firstCP[i].y,
            secondCP[i].x, secondCP[i].y,
            knotsArr[i + 1].x, knotsArr[i + 1].y
        )
    }

    return p


}

