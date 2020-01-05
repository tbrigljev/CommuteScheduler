package com.mobop.commutescheduler
import android.content.Context;
import android.graphics.*;
import android.graphics.Paint.Style;

import android.util.AttributeSet;
import android.view.*;
import com.mobop.commutescheduler.FragmentQuick.Companion.endX
import com.mobop.commutescheduler.FragmentQuick.Companion.endY
import com.mobop.commutescheduler.FragmentQuick.Companion.startX
import com.mobop.commutescheduler.FragmentQuick.Companion.startY


class FingerLine @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr){



    private var mPaint: Paint?=null

    init{
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint!!.style = Style.STROKE
        mPaint!!.color = Color.RED
        mPaint!!.strokeWidth=3.toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawLine(startX, startY, endX, endY, mPaint!!)
        fillArrow(mPaint!!,canvas,startX, startY, endX, endY)
    }


    private fun fillArrow(
        paint: Paint,
        canvas: Canvas,
        x0: Float,
        y0: Float,
        x1: Float,
        y1: Float
    ) {
        paint.style = Paint.Style.STROKE

        val arrowHeadLenght = 30
        val arrowHeadAngle = 45
        val linePts = floatArrayOf(x1 - arrowHeadLenght, y1, x1, y1)
        val linePts2 = floatArrayOf(x1, y1, x1, y1 + arrowHeadLenght)
        val rotateMat = Matrix()

        //get the center of the line

        //set the angle
        val angle =
            Math.atan2((y1 - y0).toDouble(), (x1 - x0).toDouble()) * 180 / Math.PI + arrowHeadAngle

        //rotate the matrix around the center
        rotateMat.setRotate(angle.toFloat(), x1, y1)
        rotateMat.mapPoints(linePts)
        rotateMat.mapPoints(linePts2)

        canvas.drawLine(linePts[0], linePts[1], linePts[2], linePts[3], paint)
        canvas.drawLine(linePts2[0], linePts2[1], linePts2[2], linePts2[3], paint)
    }




}

