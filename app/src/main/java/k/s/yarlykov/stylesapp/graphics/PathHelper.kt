package k.s.yarlykov.stylesapp.graphics

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Path

/**
 * Функция строит Path для наконечника стрелки, направленной вправо.
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

/**
 * https://www.vecteezy.com/vector-art/625548-city-skyline-in-minimalist-style
 */
fun cityOutlinedPath( contourData: String,  contourWidth: Float,  screen: Pair<Int, Int>) : Path {

//    val pathData = "0.150,30.150,30.86,55.86,55.150,65.150,65.50,70.50,70.35,87.15,105.35,105.50,110.50,110.150,120.150,120.90,125.90,125.80,145.85,145.93,150.94,150.150,158.150,158.115,166.115,166.96,175.96,175.75,186.74,187.60,192.60,192.39,192.60,198.60,198.75,209.75,210.96,218.96,219.114,226.114,226.150,235.150,235.15,270.33,270.150,280.150,280.90,287.90,287.77,297.77,297.58,297.77,306.77,306.90,313.90,313.150,325.150,325.56,336.56,336.33,342.33,342.13,363.13,364.33,370.33,370.55,380.55,380.150,390.150,390.50,425.50,425.150,434.150,433.90,440.90,440.80,457.80,457.90,464.90,464.100,476.100,476.113,484.113,484.150,510.150"

    // Растягиваем контур по всей ширине экрана. Для это умножаем все Х-координаты на ratio
    val scaleX = screen.first.toFloat() / contourWidth

    val pairs = contourData.split(",").toList()
    val p = Path()
    p.moveTo(0f, 150f)

    pairs.map {rawPair ->
        val arr = rawPair.split(".").toTypedArray()
        Pair(arr[0].toFloat(), arr[1].toFloat())
    }.forEach{ (x, y) ->
        p.lineTo(x * scaleX, y)
    }

    return p
}

