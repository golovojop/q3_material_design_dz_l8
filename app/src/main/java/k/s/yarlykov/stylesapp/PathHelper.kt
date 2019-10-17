package k.s.yarlykov.stylesapp

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

fun getFlightPath(): Path {
    val icFlight =
        "M21,16v-2l-8,-5V3.5c0,-0.83 -0.67,-1.5 -1.5,-1.5S10,2.67 10,3.5V9l-8,5v2l8,-2.5V19l-2,1.5V22l3.5,-1 3.5,1v-1.5L13,19v-5.5l8,2.5z"
    val icSend = "M2.01,21L23,12 2.01,3 2,10l15,2 -15,2z"
    return PathParser.createPathFromPathData(icSend)
}

