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
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
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

        custom_drawable.setImageDrawable(CustomDrawable(96f))
        initListView()
    }

    override fun onResume() {
        super.onResume()
        animateCustomDrawable()
    }

    private fun animateCustomDrawable() {
        val customDrawable : CustomDrawable = custom_drawable.drawable as CustomDrawable
//
//        ObjectAnimator.ofFloat(customDrawable, CustomDrawable.Companion.PROGRESS, 0f, 1f).apply {
//            duration = 8000L
//            interpolator = LinearInterpolator()
//            repeatCount = INFINITE
//            repeatMode = RESTART
//        }.start()

        ObjectAnimator.ofFloat(customDrawable, CustomDrawable.Companion.DOT_PROGRESS, 0f, 1f).apply {
            duration = 8000L
            interpolator = LinearInterpolator()
            repeatCount = INFINITE
            repeatMode = RESTART
        }.start()
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
