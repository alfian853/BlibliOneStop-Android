package com.gdn.android.onestop.chat.util

import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gdn.android.onestop.base.util.ItemClickCallback
import com.gdn.android.onestop.base.util.toAliasName
import com.gdn.android.onestop.base.util.toDateTime24String
import com.gdn.android.onestop.base.util.toTimeString
import com.gdn.android.onestop.chat.R
import com.gdn.android.onestop.chat.data.GroupChat
import com.gdn.android.onestop.chat.databinding.*
import com.google.android.material.button.MaterialButton


abstract class BaseChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    abstract fun onBindViewHolder(chat: GroupChat, position: Int, isDiffDay: Boolean, chatMaxWidth: Int, myChatMaxWidth: Int)

    fun setDateSeparator(
        dateContainer: CardView,
        dateText: TextView,
        chat: GroupChat,
        isDiffDay: Boolean
    ){

        if(isDiffDay){
            dateContainer.visibility = View.VISIBLE
            dateText.text = chat.dayOfYear
        }
        else{
            dateContainer.visibility = View.GONE
        }

    }
}

class ChatViewHolder(
    val binding: ItemChatBinding,
    private val onProfileClick: ItemClickCallback<String>,
    private val onMessageLongClick: ItemClickCallback<String>,
    private val repliedClickCallback: ItemClickCallback<GroupChat>,
    private val meetingNoteClickCallback: ItemClickCallback<GroupChat>
) : BaseChatViewHolder(binding.root){
    private val tvName: TextView = binding.tvUsername
    private val tvMessage: TextView = binding.tvMessage
    private val tvDate: TextView = binding.tvDate
    private val tvNamePict: TextView = binding.ivUser
    private val cvBatchDate: CardView = binding.batchDate.cvBatchDate
    private val tvBatchDate: TextView = binding.batchDate.tvBatchDate

    override fun onBindViewHolder(chat: GroupChat, position: Int, isDiffDay: Boolean, chatMaxWidth: Int, myChatMaxWidth: Int){
        tvName.text = chat.username
        tvName.setTextColor(chat.nameColor)
        tvDate.text = chat.createdAt.toTimeString()
        tvMessage.text = chat.text
        val nameAlias = chat.username.toAliasName()
        tvNamePict.text = nameAlias
        tvNamePict.setBackgroundColor(chat.nameColor)

        tvMessage.maxWidth = chatMaxWidth

        setDateSeparator(cvBatchDate, tvBatchDate, chat, isDiffDay)

        tvName.setOnClickListener { onProfileClick.onItemClick(chat.username, position) }
        tvNamePict.setOnClickListener { onProfileClick.onItemClick(chat.username, position) }
        binding.root.setOnLongClickListener {
            onMessageLongClick.onItemClick(chat.text, position)
            true
        }
    }
}

class MyChatViewHolder(
    val binding: ItemChatUserBinding,
    private val onProfileClick: ItemClickCallback<String>,
    private val onMessageLongClick: ItemClickCallback<String>,
    private val repliedClickCallback: ItemClickCallback<GroupChat>,
    private val meetingNoteClickCallback: ItemClickCallback<GroupChat>
) : BaseChatViewHolder(binding.root){
    private val tvMessage: TextView = binding.tvMessage
    private val tvDate: TextView = binding.tvDate
    private val pbSending: ProgressBar = binding.pbSending
    private val cvBatchDate: CardView = binding.batchDate.cvBatchDate
    private val tvBatchDate: TextView = binding.batchDate.tvBatchDate

    override fun onBindViewHolder(chat: GroupChat, position: Int, isDiffDay: Boolean, chatMaxWidth: Int, myChatMaxWidth: Int) {
        tvMessage.text = chat.text
        tvDate.text = chat.createdAt.toTimeString()

        if(chat.isSending){
            tvDate.visibility = View.GONE
            pbSending.visibility = View.VISIBLE
        }
        else{
            tvDate.visibility = View.VISIBLE
            pbSending.visibility = View.GONE
        }

        setDateSeparator(cvBatchDate, tvBatchDate, chat, isDiffDay)

        tvMessage.maxWidth = myChatMaxWidth

        binding.root.setOnLongClickListener {
            onMessageLongClick.onItemClick(chat.text, position)
            true
        }
    }
}

class ChatReplyViewHolder(
    val binding: ItemChatReplyBinding,
    private val onProfileClick: ItemClickCallback<String>,
    private val onMessageLongClick: ItemClickCallback<String>,
    private val repliedClickCallback: ItemClickCallback<GroupChat>,
    private val meetingNoteClickCallback: ItemClickCallback<GroupChat>
) : BaseChatViewHolder(binding.root){
    private val tvName: TextView = binding.tvUsername
    private val tvMessage: TextView = binding.tvMessage
    private val tvDate: TextView = binding.tvDate
    private val tvNamePict: TextView = binding.ivUser
    private val tvReplyName: TextView = binding.tvReplyUsername
    private val tvReplyText: TextView = binding.tvReplyMessage
    private val llReplyContainer: LinearLayout = binding.llReplyContainer
    private val cvBatchDate: CardView = binding.batchDate.cvBatchDate
    private val tvBatchDate: TextView = binding.batchDate.tvBatchDate

    override fun onBindViewHolder(chat: GroupChat, position: Int, isDiffDay: Boolean, chatMaxWidth: Int, myChatMaxWidth: Int){
        tvName.text = chat.username
        tvName.setTextColor(chat.nameColor)
        tvDate.text = chat.createdAt.toTimeString()
        tvMessage.text = chat.text
        val nameAlias = chat.username.toAliasName()
        tvNamePict.text = nameAlias
        tvNamePict.setBackgroundColor(chat.nameColor)

        tvReplyName.text = chat.repliedUsername
        tvReplyText.text = chat.repliedText
        tvReplyName.setTextColor(chat.repliedNameColor)
        llReplyContainer.setOnClickListener{
            repliedClickCallback.onItemClick(chat, position)
        }

        tvMessage.maxWidth = chatMaxWidth

        setDateSeparator(cvBatchDate, tvBatchDate, chat, isDiffDay)

        tvName.setOnClickListener { onProfileClick.onItemClick(chat.username, position) }
        tvNamePict.setOnClickListener { onProfileClick.onItemClick(chat.username, position) }
        binding.root.setOnLongClickListener {
            onMessageLongClick.onItemClick(chat.text, position)
            true
        }

    }

}

class MyChatReplyViewHolder(
    val binding: ItemChatReplyUserBinding,
    private val onProfileClick: ItemClickCallback<String>,
    private val onMessageLongClick: ItemClickCallback<String>,
    private val repliedClickCallback: ItemClickCallback<GroupChat>,
    private val meetingNoteClickCallback: ItemClickCallback<GroupChat>
) : BaseChatViewHolder(binding.root){
    private val tvMessage: TextView = binding.tvMessage
    private val tvDate: TextView = binding.tvDate
    private val pbSending: ProgressBar = binding.pbSending
    private val tvReplyName: TextView = binding.tvReplyUsername
    private val tvReplyText: TextView = binding.tvReplyMessage
    private val llReplyContainer: LinearLayout = binding.llReplyContainer
    private val cvBatchDate: CardView = binding.batchDate.cvBatchDate
    private val tvBatchDate: TextView = binding.batchDate.tvBatchDate

    override fun onBindViewHolder(chat: GroupChat, position: Int, isDiffDay: Boolean, chatMaxWidth: Int, myChatMaxWidth: Int) {
        tvMessage.text = chat.text
        tvDate.text = chat.createdAt.toTimeString()

        if(chat.isSending){
            tvDate.visibility = View.GONE
            pbSending.visibility = View.VISIBLE
        }
        else{
            tvDate.visibility = View.VISIBLE
            pbSending.visibility = View.GONE
        }

        setDateSeparator(cvBatchDate, tvBatchDate, chat, isDiffDay)

        tvReplyName.text = chat.repliedUsername
        tvReplyText.text = chat.repliedText
        tvReplyName.setTextColor(chat.repliedNameColor)
        llReplyContainer.setOnClickListener{
            repliedClickCallback.onItemClick(chat, position)
        }
        tvMessage.maxWidth = myChatMaxWidth
        binding.root.setOnLongClickListener {
            onMessageLongClick.onItemClick(chat.text, position)
            true
        }

    }
}

class MeetingViewHolder(
    val binding: ItemChatMeetingBinding,
    private val onProfileClick: ItemClickCallback<String>,
    private val onMessageLongClick: ItemClickCallback<String>,
    private val repliedClickCallback: ItemClickCallback<GroupChat>,
    private val meetingNoteClickCallback: ItemClickCallback<GroupChat>
) : BaseChatViewHolder(binding.root){
    private val tvName: TextView = binding.tvUsername
    private val tvMessage: TextView = binding.tvMessage
    private val tvTitle: TextView = binding.tvMeetingTitle
    private val tvDate: TextView = binding.tvDate
    private val tvNamePict: TextView = binding.ivUser
    private val tvMeetingDate: TextView = binding.tvMeetingDate
    private val btnSeeNote: MaterialButton = binding.btnSeeNote
    private val cvBatchDate: CardView = binding.batchDate.cvBatchDate
    private val tvBatchDate: TextView = binding.batchDate.tvBatchDate

    override fun onBindViewHolder(chat: GroupChat, position: Int, isDiffDay: Boolean, chatMaxWidth: Int, myChatMaxWidth: Int) {
        tvTitle.text = "Meeting #${chat.meetingNo}"
        tvName.text = chat.username
        tvName.setTextColor(chat.nameColor)
        tvDate.text = chat.createdAt.toTimeString()
        tvMessage.text = chat.text
        val nameAlias = chat.username.toAliasName()
        tvNamePict.text = nameAlias
        tvNamePict.setBackgroundColor(chat.nameColor)

        tvMeetingDate.text = "Date: "+chat.meetingDate?.toDateTime24String()
        btnSeeNote.setOnClickListener {
            meetingNoteClickCallback.onItemClick(chat, position)
        }

        Glide.with(binding.root.context)
            .load(R.drawable.idea)
            .into(binding.ivMeetingPict)

        setDateSeparator(cvBatchDate, tvBatchDate, chat, isDiffDay)

        tvMessage.maxWidth = chatMaxWidth

        tvName.setOnClickListener { onProfileClick.onItemClick(chat.username, position) }
        tvNamePict.setOnClickListener { onProfileClick.onItemClick(chat.username, position) }
        binding.root.setOnLongClickListener {
            onMessageLongClick.onItemClick(chat.text, position)
            true
        }
    }

}

class MyMeetingViewHolder(
    val binding: ItemChatMeetingUserBinding,
    private val onProfileClick: ItemClickCallback<String>,
    private val onMessageLongClick: ItemClickCallback<String>,
    private val repliedClickCallback: ItemClickCallback<GroupChat>,
    private val meetingNoteClickCallback: ItemClickCallback<GroupChat>
) : BaseChatViewHolder(binding.root){
    private val tvMessage: TextView = binding.tvMessage
    private val tvTitle: TextView = binding.tvMeetingTitle
    private val tvDate: TextView = binding.tvDate
    private val pbSending: ProgressBar = binding.pbSending
    private val tvMeetingDate: TextView = binding.tvMeetingDate
    private val btnSeeNote: MaterialButton = binding.btnSeeNote
    private val cvBatchDate: CardView = binding.batchDate.cvBatchDate
    private val tvBatchDate: TextView = binding.batchDate.tvBatchDate

    override fun onBindViewHolder(chat: GroupChat, position: Int, isDiffDay: Boolean, chatMaxWidth: Int, myChatMaxWidth: Int) {
        tvTitle.text = "Meeting #${chat.meetingNo}"
        tvMessage.text = chat.text
        tvDate.text = chat.createdAt.toTimeString()

        if(chat.isSending){
            tvDate.visibility = View.GONE
            pbSending.visibility = View.VISIBLE
        }
        else{
            tvDate.visibility = View.VISIBLE
            pbSending.visibility = View.GONE
        }

        setDateSeparator(cvBatchDate, tvBatchDate, chat, isDiffDay)

        Glide.with(binding.root.context)
            .load(R.drawable.idea)
            .into(binding.ivMeetingPict)

        tvMeetingDate.text = "Date: "+chat.meetingDate?.toDateTime24String()
        btnSeeNote.setOnClickListener {
            meetingNoteClickCallback.onItemClick(chat, position)
        }

        tvMessage.maxWidth = myChatMaxWidth
        binding.root.setOnLongClickListener {
            onMessageLongClick.onItemClick(chat.text, position)
            true
        }

    }
}