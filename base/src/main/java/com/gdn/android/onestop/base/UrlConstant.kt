package com.gdn.android.onestop.base

object UrlConstant {
//    const val BASE_IP = "10.151.254.210:8080"
    const val BASE_IP = "192.168.1.20:8080"
//    const val BASE_IP = "192.168.43.111:8080"
    const val BASE_URL : String = "http://$BASE_IP"
    const val BASE_RESOURCE_URL : String = "http://$BASE_IP/download"
    const val BASE_SOCKET_URL : String = "ws://$BASE_IP/websocket"

}