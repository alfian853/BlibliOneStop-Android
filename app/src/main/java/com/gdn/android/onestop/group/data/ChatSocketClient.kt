package com.gdn.android.onestop.group.data

import android.util.Log
import com.gdn.android.onestop.base.UrlConstant.BASE_SOCKET_URL
import com.gdn.android.onestop.group.util.GroupUtil
import com.gdn.android.onestop.util.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaders
import org.springframework.messaging.simp.stomp.StompSession
import org.springframework.messaging.simp.stomp.StompSessionHandler
import org.springframework.web.socket.WebSocketHttpHeaders
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.messaging.WebSocketStompClient
import java.lang.reflect.Type


class ChatSocketClient {

    companion object {
        private const val emitChatEndpoint : String = "/emit/chat"
        private const val subscribeChatEndpoint = "/subscribe/chat"
        private var instance : ChatSocketClient? = null

        fun isConnected() : Boolean {
            return if(instance != null) instance!!.isConnected
            else false
        }

        fun getInstance(
            groupDao: GroupDao,
            sessionManager: SessionManager,
            coroutineScope: CoroutineScope): ChatSocketClient {

            var localInstance = instance
            if(localInstance == null){
                synchronized(ChatSocketClient::class.java){
                    localInstance = instance
                    if(localInstance == null){
                        instance = ChatSocketClient(groupDao, sessionManager, coroutineScope)
                        localInstance = instance
                    }
                }
            }

            return instance!!
        }
    }

    private val groupDao: GroupDao
    private val sessionManager: SessionManager
    private val coroutineScope: CoroutineScope

    constructor(
        groupDao: GroupDao,
        sessionManager: SessionManager,
        coroutineScope: CoroutineScope
    ) {
        this.groupDao = groupDao
        this.sessionManager = sessionManager
        this.coroutineScope = coroutineScope
    }

    private val stompClient = WebSocketStompClient(StandardWebSocketClient()).apply {
        this.setMessageConverter(MappingJackson2MessageConverter())

    }

    lateinit var stompSession : StompSession
    lateinit var sessionHandler : StompSessionHandler

    private var isConnected = false



    fun connect() {
            if(isConnected)return
            this.sessionHandler = object : StompSessionHandler {
                override fun getPayloadType(headers: StompHeaders): Type {
                    return GroupChatResponse::class.java
                }

                override fun afterConnected(
                    session: StompSession,
                    connectedHeaders: StompHeaders?
                ) {
                    isConnected = true
                    stompSession = session
                    val refSessionHandler = this
                    GlobalScope.launch {
                        val groupList = groupDao.getAllGroup()
                        Log.d("chat-onestop","connected to server")
                        groupList.forEach {
                            stompSession.subscribe("$subscribeChatEndpoint/${it.id}", refSessionHandler)
                        }
                    }
                }

                override fun handleException(
                    session: StompSession?,
                    command: StompCommand?,
                    headers: StompHeaders?,
                    payload: ByteArray?,
                    exception: Throwable
                ) {
                    exception.printStackTrace()
                    Log.d("chat-onestop",exception.message)
                }

                override fun handleTransportError(
                    session: StompSession?,
                    exception: Throwable
                ) {
                    exception.printStackTrace()
                    Log.d("chat-onestop",exception.message)
                }

                override fun handleFrame(
                    headers: StompHeaders?,
                    payload: Any?
                ) {
                    val response = payload as GroupChatResponse
                    Log.d("chat-onestop","receive response ${response.text}")
                    coroutineScope.launch {
                        val groupChat = GroupUtil.mapChatResponse(response)
                        groupChat.isMe = response.username == sessionManager.user!!.username

                        val groupId = headers!!.destination.substring(subscribeChatEndpoint.length+1)
                        groupChat.groupId = groupId

                        groupDao.insertGroupChat(groupChat)
                        val groupInfo = groupDao.getGroupInfo(groupId)
                        groupInfo.upperBoundTimeStamp = response.createdAt
                        groupDao.insertGroupInfo(groupInfo)
                    }
                }
            }

            val headers = WebSocketHttpHeaders()

            // use '-' as separator for avoid invalid character exception
            headers.add("Authorization", "Bearer-"+sessionManager.user!!.token)
            stompClient.connect(BASE_SOCKET_URL, headers, sessionHandler)
    }

    suspend fun sendChat(request : ChatSendRequest) {
        if (!isConnected) return
        GlobalScope.launch {
            stompSession.send(emitChatEndpoint, request)
        }
    }

    fun subscribeToGroup(groupId: String){
        if(!isConnected)return
        stompSession.subscribe("$subscribeChatEndpoint/$groupId", sessionHandler)
    }

}