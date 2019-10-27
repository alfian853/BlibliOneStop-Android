package com.gdn.android.onestop.idea.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.gdn.android.onestop.R
import com.gdn.android.onestop.app.ViewModelProviderFactory
import com.gdn.android.onestop.base.BaseFullSceenFragment
import com.gdn.android.onestop.databinding.FragmentIdeaCreateBinding
import com.gdn.android.onestop.idea.viewmodel.IdeaCreateViewModel
import com.gdn.android.onestop.util.SessionManager
import kotlinx.coroutines.launch
import javax.inject.Inject


class IdeaCreateFragment : BaseFullSceenFragment<FragmentIdeaCreateBinding>() {


    @Inject
    lateinit var viewModelProviderFactory : ViewModelProviderFactory

    @Inject
    lateinit var sessionManager: SessionManager

    lateinit var viewmodel : IdeaCreateViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewmodel = ViewModelProvider(this, viewModelProviderFactory).get(IdeaCreateViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.databinding = FragmentIdeaCreateBinding.inflate(inflater, container, false)
        databinding.viewmodel = viewmodel


        databinding.btnIdeaSubmit.setOnClickListener {
            viewmodel.viewModelScope.launch {
                viewmodel.postIdea()
                fragmentManager!!.beginTransaction().remove(this@IdeaCreateFragment).commit()
            }
        }

        databinding.etContent.doOnTextChanged { text, _, _, _ ->

            if(text == null || text.isEmpty()){
                databinding.btnIdeaSubmit.isEnabled = false
                databinding.btnIdeaSubmit.setTextColor(
                    ResourcesCompat.getColor(resources, R.color.faded_blue, null)
                )
            }
            else{
                databinding.btnIdeaSubmit.isEnabled = true
                databinding.btnIdeaSubmit.setTextColor(
                    ResourcesCompat.getColor(resources, R.color.colorPrimary, null)
                )
            }
        }

        databinding.tvUsername.text = sessionManager.user!!.username

        return databinding.root
    }


}