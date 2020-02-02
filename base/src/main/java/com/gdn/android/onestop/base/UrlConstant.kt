package com.gdn.android.onestop.base

object UrlConstant {
//    const val BASE_IP = "10.151.253.230:8080"
    const val BASE_IP = "192.168.1.25"
//    const val BASE_IP = "192.168.43.111:8080"
    const val BASE_URL : String = "http://$BASE_IP:8080"
    const val BASE_RESOURCE_URL : String = "http://$BASE_IP/download"
    const val BASE_SOCKET_URL : String = "ws://$BASE_IP/websocket"

}