package com.rkgroup.adsmanager

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import android.widget.TextView
import android.util.DisplayMetrics




class AdContainer(mContext: Context, attrs: AttributeSet?) : FrameLayout(mContext, attrs) {
    constructor(mContext: Context) : this(mContext, null)

    private val adLoadMSg: TextView = TextView(mContext)

    init {
        adLoadMSg.gravity = Gravity.CENTER
        adLoadMSg.text = mContext.getString(R.string.ad_loading_msg)
        removeAllViews()
        addView(adLoadMSg, LayoutParams(MATCH_PARENT, MATCH_PARENT).also {
            it.gravity = Gravity.CENTER
        })
       minimumHeight = getDefaultMinimumHeight().toInt()
    }

    private fun getDefaultMinimumHeight(dp: Float = 60f): Float {
        return dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }


}