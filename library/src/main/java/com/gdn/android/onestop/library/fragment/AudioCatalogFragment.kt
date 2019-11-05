package com.gdn.android.onestop.library.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gdn.android.onestop.base.BaseFragment
import com.gdn.android.onestop.library.databinding.LayoutPageAudioBinding


class AudioCatalogFragment : BaseFragment<LayoutPageAudioBinding>() {
    override fun doFragmentInjection() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        databinding = LayoutPageAudioBinding.inflate(inflater, container, false)

        return databinding.root
    }
}