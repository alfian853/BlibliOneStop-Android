package com.gdn.android.onestop.chat.data

import com.gdn.android.onestop.base.util.SessionManager
import com.gdn.android.onestop.chat.util.ChatUtil
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min


class GroupChatRepository @Inject constructor(
    private val chatDao: ChatDao,
    private val groupDao: GroupDao,
    private val groupClient: ChatClient,
    private val sessionManager: SessionManager
) {


  companion object {
    private const val PAGE_SIZE = 20
  }

  private val sessionUsername: String by lazy {sessionManager.user!!.username}

  fun getChatLiveData(groupId: String) = chatDao.getGroupChatLiveData(groupId)

  private suspend fun mapChatToMeeting(groupChat: GroupChat): GroupMeeting {
    return GroupMeeting().apply {
      chatId = groupChat.id
      groupId = groupChat.groupId
      meetingDate = groupChat.meetingDate!!
      meetingNo = groupChat.meetingNo!!
      groupName = groupDao.getGroupById(groupChat.groupId).name
    }
  }

  suspend fun addAndProcessGroupChat(groupChat: GroupChat){
    chatDao.insertGroupChat(groupChat)
    if(groupChat.isMeeting){
      groupDao.insertGroupMeeting(mapChatToMeeting(groupChat))
    }
  }

  suspend fun addAndProcessGroupChat(chatList: List<GroupChat>){
    chatDao.insertGroupChat(chatList)
    val meetingList = chatList.filter { it.isMeeting }.map {mapChatToMeeting(it)}
    groupDao.insertGroupMeeting(meetingList)
  }

  suspend fun loadMoreChatBefore(groupId : String){
    val groupInfo = groupDao.getGroupInfo(groupId)
    if(groupInfo.hasFetchFirstChat)return
    val response = groupClient.getGroupChat(groupId, groupInfo.lowerBoundTimeStamp, null,
        PAGE_SIZE
    )

    if(response.isSuccessful){
      response.body()?.data?.let {
        if(it.isNotEmpty()){

          var minTime = groupInfo.lowerBoundTimeStamp
          var maxTime = groupInfo.upperBoundTimeStamp

          it.forEach {
            maxTime = max(it.createdAt, maxTime)
            minTime = min(it.createdAt, minTime)
          }

          groupInfo.lowerBoundTimeStamp = minTime
          groupInfo.upperBoundTimeStamp = maxTime

          groupDao.insertGroupInfo(groupInfo)
          val chatList : List<GroupChat> = it.map {
            ChatUtil.mapGroupChatResponse(it, sessionUsername).apply {
              this.groupId = groupId
            }
          }
          addAndProcessGroupChat(chatList)
        }
        else{
          groupInfo.hasFetchFirstChat = true
          groupDao.insertGroupInfo(groupInfo)
        }
      }
    }
  }

  suspend fun loadMoreChatAfter(groupId: String){
    val groupInfo = groupDao.getGroupInfo(groupId)

    val response = groupClient.getGroupChat(groupId, null, groupInfo.upperBoundTimeStamp,
        PAGE_SIZE
    )

    if(response.isSuccessful){
      response.body()?.data?.let {
        if(it.isNotEmpty()){

          var minTime = groupInfo.lowerBoundTimeStamp
          var maxTime = groupInfo.upperBoundTimeStamp

          it.forEach {
            maxTime = max(it.createdAt, maxTime)
            minTime = min(it.createdAt, minTime)
          }

          groupInfo.lowerBoundTimeStamp = minTime
          groupInfo.upperBoundTimeStamp = maxTime

          groupDao.insertGroupInfo(groupInfo)
          val chatList : List<GroupChat> = it.map {
            ChatUtil.mapGroupChatResponse(it, sessionUsername).apply {
              this.groupId = groupId
              this.isMe = this.username == sessionUsername
            }
          }
          addAndProcessGroupChat(chatList)
        }
      }
    }
  }

  suspend fun sendChat(groupId: String, chatSendRequest: GroupChatSendRequest): Boolean {

    val response = groupClient.postGroupChat(groupId, chatSendRequest)

    if(response.isSuccessful){
      val groupChat = ChatUtil.mapGroupChatResponse(response.body()!!.data!!, sessionUsername).apply {
        this.groupId = groupId
      }
      chatDao.insertGroupChat(groupChat)
    }

    return response.isSuccessful
  }

  suspend fun nukeGroupDatabase(){
    groupDao.deleteAllGroup()
    groupDao.deleteAllGroupChat()
    groupDao.deleteAllGroupInfo()
    groupDao.deleteAllGroupMeeting()
    groupDao.deleteAllMeetingNote()
  }

}