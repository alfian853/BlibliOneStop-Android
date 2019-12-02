package com.gdn.android.onestop.group.fragment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.gdn.android.onestop.base.util.FragmentActionCallback
import com.gdn.android.onestop.base.util.toDateString
import com.gdn.android.onestop.base.util.toTime24String
import com.gdn.android.onestop.group.databinding.DialogMeetingCreateBinding
import java.util.*

class MeetingCreateFragment(
    private val fragmentActionCallback : FragmentActionCallback<MeetingCreateData>
) : DialogFragment() {


  private val meetingCreateData = MeetingCreateData()

  private lateinit var databinding: DialogMeetingCreateBinding

  override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
  ): View? {
    databinding = DialogMeetingCreateBinding.inflate(inflater, container, false)
    databinding.meetingdata = meetingCreateData
    val calender = Calendar.getInstance()

    databinding.llDateBox.setOnClickListener {
      DatePickerDialog(
          this.context!!,
          DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            calender.set(year, month, dayOfMonth)
            databinding.tvDate.text = calender.timeInMillis.toDateString()
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
            databinding.tvTime.text = calender.timeInMillis.toTime24String()
          },
          calender.get(Calendar.HOUR_OF_DAY),
          calender.get(Calendar.MINUTE),
          true
      ).show()
    }

    databinding.btnClose.setOnClickListener {
      fragmentManager!!.beginTransaction().remove(this).commit()
    }

    databinding.btnSubmit.setOnClickListener {
      meetingCreateData.datetime = calender.timeInMillis
      fragmentActionCallback.onActionSuccess(meetingCreateData)
      fragmentManager!!.beginTransaction().remove(this).commit()
    }

    return databinding.root
  }
}