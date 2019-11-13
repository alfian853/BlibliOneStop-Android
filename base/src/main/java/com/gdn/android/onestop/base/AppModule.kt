package com.gdn.android.onestop.base

import android.content.Context
import android.content.Intent
import android.util.Log
import com.fasterxml.jackson.databind.ObjectMapper
import com.gdn.android.onestop.base.util.Navigator
import com.gdn.android.onestop.base.util.NetworkUtil
import com.gdn.android.onestop.base.util.SessionManager
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.CallAdapter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import javax.inject.Singleton


@Module
class AppModule{

    @Provides
    @Singleton
    fun provideNetworkUtil(context: Context) : NetworkUtil{
        return NetworkUtil(context)
    }

    @Provides
    @Singleton
    fun provideRetrofit(sessionManager: SessionManager, context: Context): Retrofit {
//        val logger = HttpLoggingInterceptor()
//        logger.level = HttpLoggingInterceptor.Level.BODY
        val clientBuilder = OkHttpClient.Builder()

//        clientBuilder.addInterceptor(logger)
        clientBuilder.addInterceptor { chain ->

            var request = chain.request()
            if(sessionManager.isLoggedIn) {
                request = request.newBuilder()
                    .addHeader("Authorization", "Bearer " + sessionManager.user!!.token).build()
            }

            try{
                val response = chain.proceed(request)
                if(response.code == 403 || response.code == 401){
                    sessionManager.logout()
                    val intent = Navigator.getIntent(Navigator.Destination.LOGIN)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    context.startActivity(intent)
                }
                response
            }
            catch (e : Exception){
                sessionManager.logout()
                val intent = Navigator.getIntent(Navigator.Destination.LOGIN)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
                okhttp3.Response.Builder().request(request)
                    .protocol(Protocol.H2_PRIOR_KNOWLEDGE)
                    .code(503).message("Service unavailable!").build()
            }

        }


        return Retrofit.Builder().baseUrl(UrlConstant.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(clientBuilder.build())
            .build()
    }
}