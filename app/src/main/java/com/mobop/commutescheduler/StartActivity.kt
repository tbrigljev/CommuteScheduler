package com.mobop.commutescheduler

/* TO USE FOR FIRST TIME USE
https://www.androidhive.info/2016/05/android-build-intro-slider-app/
 */

/* Import ******************************************************** */

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.text.HtmlCompat
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager


/* *************************************************************** */

/* Global variables ********************************************** */
var mAdapter : StartActivity.ViewPagerAdapter? = null
lateinit var slides : IntArray
lateinit var layoutDots : LinearLayout
lateinit var startNext : Button
lateinit var startSkip : Button
lateinit var viewSlides : ViewPager
/* *************************************************************** */

/* *************************************************************** */
/* StartActivity ************************************************* */
/* *************************************************************** */
class StartActivity : AppCompatActivity(){

    private lateinit var prefManager : PrefManager

    override fun onCreate(savedInstanceState : Bundle?) {
        // Check first launch
        prefManager = PrefManager(this)
        if (!prefManager.isFirstTimeLaunch()) {
            launchHomeScreen()
            finish()
        }

        // Hide toolbar

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_start)

        viewSlides = findViewById(R.id.view_slides)
        layoutDots  = findViewById(R.id.layout_dots)
        startNext = findViewById(R.id.start_button_next)
        startSkip = findViewById(R.id.start_button_skip)

        slides = intArrayOf(
            R.layout.start01_welcome,
            R.layout.start02_mainview,
            R.layout.start03_addcommute
        )

        navigationDots(0)

        mAdapter = StartActivity.ViewPagerAdapter()
        viewSlides.adapter = mAdapter
        viewSlides.addOnPageChangeListener(slideChangeListener)

        startSkip.setOnClickListener { launchHomeScreen() }

        startNext.setOnClickListener {
            // checking for last page
            // if last page home screen will be launched
            val current : Int = getItem(+1)
            if (current < slides.count()) { // move to next screen
                viewSlides.currentItem = current
            } else {
                launchHomeScreen()
            }
        }
    }

    private fun navigationDots(currentPage : Int){
        var dots : MutableList<TextView> =
            MutableList(slides.count(), { index -> TextView(this) } )

        layoutDots.removeAllViews()
        for(dot in dots){
            dot.text = HtmlCompat.fromHtml("&#8226;", HtmlCompat.FROM_HTML_MODE_LEGACY)
            dots[currentPage].setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
            layoutDots.addView(dot)
        }
        if(dots.count() > 0){
            dots[currentPage].setTextColor(ContextCompat.getColor(this, R.color.colorCommutesOdd))
        }
    }

    private fun getItem(i : Int) : Int {
        return viewSlides.currentItem + i
    }

    // After setting first launch detection
    private fun launchHomeScreen() {
        prefManager.setFirstTimeLaunch(false)
        startActivity(Intent(this@StartActivity, MainActivity::class.java))
        finish()
    }

    var slideChangeListener: ViewPager.OnPageChangeListener = object :
        ViewPager.OnPageChangeListener {

        override fun onPageSelected(position: Int) {
            navigationDots(position)
            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == slides.count() - 1) { // last page. make button text to GOT IT
                startNext.text = getString(R.string.action_got)
                startSkip.visibility = View.GONE
            } else { // still pages are left
                startNext.text = getString(R.string.action_next)
                startSkip.visibility = View.VISIBLE
            }
        }

        override fun onPageScrolled(arg0 : Int, arg1 : Float, arg2 : Int) {}
        override fun onPageScrollStateChanged(arg0 : Int) {}
    }

    class ViewPagerAdapter : PagerAdapter() {

        private var layoutInflater: LayoutInflater? = null

        override fun instantiateItem(container : ViewGroup, position : Int) : Any {
            layoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?

            val view : View = layoutInflater.inflate(slides.get(position), container, false)
            container.addView(view)
            return view
        }

        override fun getCount() : Int {
            return slides.count()
        }

        override fun isViewFromObject(view : View, obj : Any) : Boolean {
            return view === obj
        }

        override fun destroyItem(
            container : ViewGroup,
            position : Int,
            `object` : Any
        ) {
            val view : View = `object` as View
            container.removeView(view)
        }
    }
}