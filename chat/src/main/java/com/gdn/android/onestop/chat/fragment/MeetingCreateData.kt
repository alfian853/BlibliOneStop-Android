package com.gdn.android.onestop.chat.fragment

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR

class MeetingCreateData: BaseObservable() {
  var datetime : Long = 0
  var description : String = ""
    @Bindable
    get(){return field}
    set(value) {
      field = value
      notifyPropertyChanged(BR.description)
    }
}