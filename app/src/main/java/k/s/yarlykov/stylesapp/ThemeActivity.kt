/**
 * Materials
 * https://developer.android.com/training/animation/reposition-view
 *
 * Create Path:
 * https://medium.com/androiddevelopers/playing-with-paths-3fbc679a6f77
 */

package k.s.yarlykov.stylesapp

import android.animation.ObjectAnimator
import android.animation.ValueAnimator.INFINITE
import android.animation.ValueAnimator.RESTART
import android.graphics.Color
import android.graphics.Path
import android.graphics.PathMeasure
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.FloatProperty
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.PathInterpolator
import android.view.animation.TranslateAnimation
import android.widget.SimpleAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_theme.*
import kotlinx.android.synthetic.main.content_styled.*

class ThemeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_theme)
        setSupportActionBar(toolbar_ic)

        with(supportActionBar!!) {
            setDisplayHomeAsUpEnabled(true)
        }

        custom_drawable.setImageDrawable(CustomDrawable(50f))

        initListView()
    }

    override fun onResume() {
        super.onResume()
        animateFlightByCycle()
    }

    private fun animateFlightByCycle() {
        val path = Path()

        path.addCircle(450f, 150f, 125f, Path.Direction.CW)
        val animator = ObjectAnimator.ofFloat(iv_flight, View.X, View.Y, path).apply {
            duration = 12000
            repeatCount = INFINITE
            repeatMode = RESTART
            start()
        }
    }




    private fun animateFlightByCurve() {


//        val coords = IntArray(2)
//        iv_flight.getLocationOnScreen(coords)
//        Log.e("APP_TAG", "On screen x:y = ${coords[0]}:${coords[1]}")
//        iv_flight.getLocationInWindow(coords)
//        Log.e("APP_TAG", "On window x:y = ${coords[0]}:${coords[1]}")

        val x = clCanvas.x + clCanvas.layoutParams.width/2
        val y = clCanvas.y + clCanvas.layoutParams.height/2

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            val path = createPath(x, y, 100, 100f)

//            val path = Path().apply {
////                arcTo(0f, 0f, 1000f, 1000f, 270f, -180f, true)
//                arcTo(0f, 0f, 100f, 100f, 270f, -180f, true)
//            }
            val animator = ObjectAnimator.ofFloat(iv_flight, View.X, View.Y, path).apply {
                duration = 12000
                start()
            }
        } else {
            animateFlightHorizontal()
        }
    }

    private fun createPath(centerX: Float, centerY: Float, sides: Int, radius: Float): Path {
        val path = Path()
        val angle = 2.0 * Math.PI / sides

        path.moveTo(
            centerX + (radius * Math.cos(0.0)).toFloat(),
            centerY + (radius * Math.sin(0.0)).toFloat()
        )
        for (i in 1 until sides) {
            path.lineTo(
                centerX + (radius * Math.cos(angle * i)).toFloat(),
                centerY + (radius * Math.sin(angle * i)).toFloat()
            )
        }
        path.close()
        return path
    }

    private fun animateFlightHorizontal() {
        iv_flight.startAnimation(
            TranslateAnimation(
                0f, 300f,
                0f, 0f
            )
                .apply {
                    duration = 1000
                })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun initListView() {
        val keyText = "text"
        val keyIcon = "icon"
        val bookingText = resources.getStringArray(R.array.flight_booking)
        val bookingPics = intArrayOf(R.drawable.ic_passenger, R.drawable.ic_from, R.drawable.ic_to, R.drawable.ic_date)

        val items = mutableListOf<Map<String, Any>>()

        for (i in bookingText.indices) {
            items.add(mapOf(keyText to bookingText[i], keyIcon to bookingPics[i]))
        }

        val from = arrayOf(keyText, keyIcon)
        val to = arrayOf(R.id.tv_item_lv, R.id.iv_item_lv).toIntArray()

        lv_options.adapter = object : SimpleAdapter(this, items, R.layout.layout_lv_item, from, to) {

            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                val view = super.getView(position, convertView, parent)
                view.findViewById<TextView>(R.id.tv_item_lv).setTextColor(if (position > 1) Color.GRAY else Color.WHITE)
                return view
            }
        }
    }
}
