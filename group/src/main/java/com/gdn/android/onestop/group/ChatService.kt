package com.gdn.android.onestop.group

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.gdn.android.onestop.group.data.ChatSocketClient
import com.gdn.android.onestop.group.data.GroupDao
import com.gdn.android.onestop.base.util.SessionManager
import dagger.android.DaggerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

/**
 * Unused class/function, for archive purpose
 */
class ChatService : DaggerService(), CoroutineScope {
    override fun onBind(intent: Intent?): IBinder? {
        throw UnsupportedOperationException("Not yet implemented")
    }

    @Inject
    lateinit var groupDao: GroupDao

    @Inject
    lateinit var sessionManager: SessionManager

    override val coroutineContext: CoroutineContext = Dispatchers.IO

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("chat-onestop","start service")
        val chatSocketClient : ChatSocketClient
                = ChatSocketClient.getInstance(groupDao, sessionManager, this)
        this.launch {
            chatSocketClient.connect()
        }
        return Service.START_STICKY
    }

    override fun onDestroy() {
        Log.d("chat-onestop","Service died :(")
        super.onDestroy()
    }
}