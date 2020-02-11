package com.gdn.android.onestop.ideation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.gdn.android.onestop.base.BaseFullScreenFragment
import com.gdn.android.onestop.base.ViewModelProviderFactory
import com.gdn.android.onestop.base.util.SessionManager
import com.gdn.android.onestop.ideation.R
import com.gdn.android.onestop.ideation.databinding.FragmentIdeaCreateBinding
import com.gdn.android.onestop.ideation.injection.IdeaComponent
import com.gdn.android.onestop.ideation.viewmodel.IdeaCreateViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


class IdeaCreateFragment : BaseFullScreenFragment<FragmentIdeaCreateBinding>() {

    @Inject
    lateinit var viewModelProviderFactory : ViewModelProviderFactory

    @Inject
    lateinit var sessionManager: SessionManager

    lateinit var viewmodel : IdeaCreateViewModel

    override fun doFragmentInjection() {
        IdeaComponent.getInstance().inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewmodel = ViewModelProvider(this, viewModelProviderFactory).get(IdeaCreateViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        databinding = FragmentIdeaCreateBinding.inflate(inflater, container, false)
        databinding.viewmodel = viewmodel

        databinding.tvUser.text = sessionManager.user!!.alias
        databinding.tvUser.setBackgroundColor(sessionManager.user!!.color)

        databinding.btnIdeaSubmit.setOnClickListener {
            viewmodel.launch {
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