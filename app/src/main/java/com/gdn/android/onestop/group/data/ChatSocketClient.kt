package com.gdn.android.onestop.group.data

import com.gdn.android.onestop.base.UrlConstant.BASE_SOCKET_URL
import com.gdn.android.onestop.util.SessionManager
import com.google.gson.Gson
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
        private val gson = Gson()
        private var instance : ChatSocketClient? = null

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
            if(isConnected)return //it.onSuccess(Unit)
            this.sessionHandler = object : StompSessionHandler {
                override fun getPayloadType(headers: StompHeaders): Type {
                    return GroupChat::class.java
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
                }

                override fun handleTransportError(
                    session: StompSession?,
                    exception: Throwable
                ) {
                    exception.printStackTrace()
                }


                override fun handleFrame(
                    headers: StompHeaders?,
                    payload: Any?
                ) {
                    val response = payload as GroupChat
                    val groupId = headers!!.destination.substring(subscribeChatEndpoint.length+1)
                    response.groupId = groupId
                    coroutineScope.launch {
                        response.isMe = response.username == sessionManager.user!!.username
                        groupDao.insertGroupChat(response)
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
            val gson = Gson()
            stompSession.send(emitChatEndpoint, request)
        }
    }

    fun subscribeToGroup(groupId: String){
        if(!isConnected)return
        stompSession.subscribe("$subscribeChatEndpoint/$groupId", sessionHandler)
    }

}