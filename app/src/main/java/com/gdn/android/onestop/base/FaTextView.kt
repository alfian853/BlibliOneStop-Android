package com.gdn.android.onestop.base

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.widget.TextView


class FaTextView : TextView{


    constructor(context: Context) : super(context){
        createView()
    }

    constructor(context: Context, attributeSet: AttributeSet)
            : super(context, attributeSet){
        createView()
    }

    private fun createView(){
        typeface = Typeface.createFromAsset(context.assets,"Font Awesome 5 Free-Regular-400.otf")

    }
}