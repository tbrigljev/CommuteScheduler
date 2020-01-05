/* Inspired by and adapted from
https://www.androidhive.info/2016/05/android-build-intro-slider-app/
 */

package com.mobop.commutescheduler

/* Import ******************************************************** */
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
var callingFromSettings : Boolean = false
/* *************************************************************** */

/* *************************************************************** */
/* StartActivity ************************************************* */
/* *************************************************************** */
class StartActivity : AppCompatActivity(){

    private lateinit var prefManager : PrefManager

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)

        prefManager = PrefManager(this)
        if((!prefManager.isFirstTimeLaunch()) and
            (!callingFromSettings)){
            callingFromSettings = false
            launchHomeScreen()
            finish()
        }

        setContentView(R.layout.activity_start)

        viewSlides = findViewById(R.id.view_slides)
        layoutDots  = findViewById(R.id.layout_dots)
        startNext = findViewById(R.id.start_button_next)
        startSkip = findViewById(R.id.start_button_skip)

        slides = intArrayOf(
            R.layout.start80,
            R.layout.start80,
            R.layout.start80,
            R.layout.start80,
            R.layout.start80,
            R.layout.start98,
            R.layout.start80,
            R.layout.start80,
            R.layout.start80,
            R.layout.start70
        )

        navigationDots(0)

        mAdapter = ViewPagerAdapter(this)
        mAdapter!!.setActivity(this)

        viewSlides.adapter = mAdapter
        viewSlides.addOnPageChangeListener(slideChangeListener)

        startSkip.setOnClickListener { launchHomeScreen() }

        startNext.setOnClickListener {
            val current : Int = getItem(+ 1)
            if(current < slides.count()){
                viewSlides.currentItem = current
            } else {
                launchHomeScreen()
            }
        }
    }

    private fun navigationDots(currentPage : Int){
        val dots : MutableList<TextView> =
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

    private fun launchHomeScreen() {
        prefManager.setFirstTimeLaunch(false)
        startActivity(Intent(this@StartActivity, MainActivity::class.java))
        finish()
    }

    private var slideChangeListener :
            ViewPager.OnPageChangeListener = object :
        ViewPager.OnPageChangeListener {
        override fun onPageSelected(position: Int){
            navigationDots(position)
            if(position == slides.count() - 1){
                startNext.text = getString(R.string.action_got)
                startSkip.visibility = View.GONE
            } else {
                startNext.text = getString(R.string.action_next)
                startSkip.visibility = View.VISIBLE
            }
        }

        override fun onPageScrolled(arg0 : Int, arg1 : Float, arg2 : Int){}
        override fun onPageScrollStateChanged(arg0 : Int){}
    }

    class ViewPagerAdapter : PagerAdapter{

        private var layoutInflater : LayoutInflater? = null
        private var mActivity : StartActivity? = null
        private var images : IntArray
        private var texts : IntArray

        constructor(context : Context):super(){
            this.images = intArrayOf(
                R.drawable.start01,
                R.drawable.start02,
                R.drawable.start03,
                R.drawable.start04,
                R.drawable.start05,
                R.drawable.start06,
                R.drawable.start07,
                R.drawable.start08,
                R.drawable.start09,
                R.drawable.start10
            )
            this.texts = intArrayOf(
                R.string.start01,
                R.string.start02,
                R.string.start03,
                R.string.start04,
                R.string.start05,
                R.string.start06,
                R.string.start07,
                R.string.start08,
                R.string.start09,
                R.string.start10
            )
        }

        fun setActivity(activity : StartActivity){
            mActivity = activity
        }
        override fun instantiateItem(container : ViewGroup, position : Int) : Any {
            val image : ImageView
            val text : TextView

            layoutInflater =
                mActivity!!.applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)
                        as LayoutInflater?

            val view : View = layoutInflater!!.inflate(slides[position], container, false)

            image = view.findViewById(R.id.start_image)
            image.setImageResource(images[position])

            text = view.findViewById(R.id.start_text)
            text.setText(texts[position])

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
        ){
            val view : View = `object` as View
            container.removeView(view)
        }
    }
}