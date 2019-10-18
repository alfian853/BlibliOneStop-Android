package com.gdn.android.onestop.channel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gdn.android.onestop.base.BaseFragment
import com.gdn.android.onestop.databinding.FragmentChannelBinding

class ChannelFragment : BaseFragment<FragmentChannelBinding>() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        this.databinding = FragmentChannelBinding.inflate(inflater, container, false)
        return databinding.root
    }
}