package com.gdn.android.onestop.chat.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.gdn.android.onestop.chat.ChatActivity
import com.gdn.android.onestop.chat.ChatActivityArgs
import com.gdn.android.onestop.chat.R
import com.gdn.android.onestop.chat.data.ChatDao
import com.gdn.android.onestop.chat.data.PersonalChatRepository
import com.gdn.android.onestop.chat.data.PersonalInfo
import com.gdn.android.onestop.chat.databinding.FragmentBsPersonalOptionBinding
import com.gdn.android.onestop.chat.util.ChatUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PersonalOptionFragment(
    val personalInfo: PersonalInfo,
    private val chatRepository: PersonalChatRepository,
    private val chatDao: ChatDao
) : BottomSheetDialogFragment() {

    lateinit var binding: FragmentBsPersonalOptionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBsPersonalOptionBinding.inflate(inflater, container, false)

        binding.tvUsername.text = personalInfo.name

        val chatSendOnClick = View.OnClickListener {
            val arg = ChatActivityArgs(null, personalInfo)

            val intent = Intent(activity, ChatActivity::class.java)
            intent.putExtras(arg.toBundle())
            startActivity(intent)
        }

        binding.ivChat.setOnClickListener(chatSendOnClick)
        binding.tvChat.setOnClickListener(chatSendOnClick)

        val soundOnClick = View.OnClickListener {
            personalInfo.isMute = !personalInfo.isMute
            CoroutineScope(Dispatchers.IO).launch {
                chatDao.insertPersonalInfo(personalInfo)
            }
            ChatUtil.setSoundIcon(binding.ivChat, personalInfo.isMute)
            ChatUtil.setSoundIconLabel(binding.tvMute, personalInfo.isMute)
        }

        binding.ivMute.setOnClickListener(soundOnClick)
        binding.tvMute.setOnClickListener(soundOnClick)

        val removeChatOnClick = View.OnClickListener {
            AlertDialog.Builder(this.context!!)
                .setTitle(getString(R.string.remove_chat))
                .setMessage(R.string.are_you_sure)
                .setPositiveButton(R.string.yes) { dialog, which ->
                    CoroutineScope(Dispatchers.IO).launch {
                        chatRepository.removeChat(personalInfo.name)
                        fragmentManager!!.beginTransaction().remove(this@PersonalOptionFragment).commit()
                    }
                }.setNegativeButton(R.string.no) { dialog, which -> }.show()
        }

        binding.ivRemoveChat.setOnClickListener(removeChatOnClick)
        binding.tvRemoveChat.setOnClickListener(removeChatOnClick)


        return binding.root
    }
}