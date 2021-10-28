package com.xiaoyv.widget.custom

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout

class TestView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs), InEditModeCompat {

    init {
        InEditModeCompat.init(this)
    }

    override fun initInApp(appContext: Context) {

    }

    override fun initInEditMode(editorContext: Context) {
        
    }
}