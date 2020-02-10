package com.gdn.android.onestop.chat.fragment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doOnTextChanged
import com.gdn.android.onestop.base.BaseDialogFragment
import com.gdn.android.onestop.base.util.FragmentActionCallback
import com.gdn.android.onestop.base.util.Util
import com.gdn.android.onestop.base.util.toDateString
import com.gdn.android.onestop.base.util.toTime24String
import com.gdn.android.onestop.chat.R
import com.gdn.android.onestop.chat.databinding.DialogMeetingCreateBinding
import java.util.*

class MeetingCreateFragment(
    private val fragmentActionCallback : FragmentActionCallback<MeetingCreateData>
) : BaseDialogFragment<DialogMeetingCreateBinding>() {


  private val meetingCreateData = MeetingCreateData()

  private lateinit var errorEmptyText: String
  private lateinit var errorPleaseSelectFutureDate: String
  private lateinit var errorPleaseSelectFutureTime: String

  private var redColor: Int = 0

  private var blackColor: Int = 0

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    errorEmptyText = resources.getString(R.string.please_fill_this_field)
    errorPleaseSelectFutureDate = resources.getString(R.string.error_please_select_future_date)
    errorPleaseSelectFutureTime = resources.getString(R.string.error_please_select_future_time)
  }

  override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
  ): View? {
    databinding = DialogMeetingCreateBinding.inflate(inflater, container, false)

    redColor = ResourcesCompat.getColor(resources, R.color.red, null)
    blackColor = ResourcesCompat.getColor(resources, R.color.black, null)

    databinding.meetingdata = meetingCreateData
    val calender = Calendar.getInstance()
    val currentTime = Calendar.getInstance().time

    databinding.llDateBox.setOnClickListener {
      DatePickerDialog(
          this.context!!,
          DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            calender.set(year, month, dayOfMonth)
            if(calender.time.before(currentTime)){
              databinding.tvDate.setTextColor(redColor)
              databinding.tvDate.text = errorPleaseSelectFutureDate
            }
            else{
              databinding.tvDate.text = calender.timeInMillis.toDateString()
              databinding.tvDate.setTextColor(blackColor)
            }
          },
          calender.get(Calendar.YEAR),
          calender.get(Calendar.MONTH),
          calender.get(Calendar.DAY_OF_MONTH)
      ).show()
    }

    databinding.llTimeBox.setOnClickListener {
      TimePickerDialog(
          this.context!!,
          TimePickerDialog.OnTimeSetListener{view, hourOfDay, minute ->
            calender.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calender.set(Calendar.MINUTE, minute)

            if(calender.time.before(currentTime)){
              databinding.tvTime.setTextColor(redColor)
              databinding.tvTime.text = errorPleaseSelectFutureTime
            }
            else{
              databinding.tvTime.text = calender.timeInMillis.toTime24String()
              databinding.tvTime.setTextColor(blackColor)
            }
          },
          calender.get(Calendar.HOUR_OF_DAY),
          calender.get(Calendar.MINUTE),
          true
      ).show()
    }

    databinding.btnClose.setOnClickListener {
      fragmentManager!!.beginTransaction().remove(this).commit()
    }

    databinding.etDescription.doOnTextChanged { text, start, count, after ->
      if(Util.isNotEmpty(text)){
        databinding.tilDescription.error = null
      }
    }

    databinding.btnSubmit.setOnClickListener {
      var hasError = false

      if(databinding.tvDate.text.isEmpty() || databinding.tvDate.currentTextColor == redColor){
        databinding.tvDate.setTextColor(redColor)
        databinding.tvDate.text = errorEmptyText
        hasError = true
      }

      if(Util.isEmpty(databinding.tvTime.text) || databinding.tvTime.currentTextColor == redColor){
        databinding.tvTime.setTextColor(redColor)
        databinding.tvTime.text = errorEmptyText
        hasError = true
      }

      if(Util.isEmpty(databinding.etDescription.text)){
        databinding.tilDescription.error = errorEmptyText
        hasError = true
      }

      if(hasError)return@setOnClickListener

      meetingCreateData.datetime = calender.timeInMillis
      fragmentActionCallback.onActionSuccess(meetingCreateData)
      fragmentManager!!.beginTransaction().remove(this).commit()
    }

    return databinding.root
  }
}