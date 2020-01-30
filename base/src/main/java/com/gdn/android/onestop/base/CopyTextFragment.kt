package com.gdn.android.onestop.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gdn.android.onestop.base.databinding.DialogCopyBinding

class CopyTextFragment(private val onCopyClick: () -> Unit) : BaseDialogFragment<DialogCopyBinding>(){

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View? {
    databinding = DialogCopyBinding.inflate(inflater, container, false)
    databinding.tvCopy.setOnClickListener {
      fragmentManager!!.beginTransaction().remove(this).commit()
      onCopyClick()
    }

    return databinding.root
  }
}