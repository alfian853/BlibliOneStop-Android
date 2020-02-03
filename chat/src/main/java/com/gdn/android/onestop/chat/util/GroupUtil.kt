package com.gdn.android.onestop.chat.util

import android.view.View
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.gdn.android.onestop.chat.R

object GroupUtil {

  fun setSoundIcon(view: View, isMute: Boolean){
    view.background = if(isMute) ResourcesCompat.getDrawable(view.resources, R.drawable.ic_sound, null)
    else ResourcesCompat.getDrawable(view.resources, R.drawable.ic_sound_disable, null)
  }

  fun setSoundIconLabel(view: TextView, isMute: Boolean){
    view.text = if(isMute)view.resources.getString(R.string.unmute_group)
    else view.resources.getString(R.string.mute_group)
  }

}

