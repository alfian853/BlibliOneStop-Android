package com.gdn.android.onestop.app

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.fragment.app.Fragment
import com.gdn.android.onestop.base.UrlConstant
import com.gdn.android.onestop.login.LoginActivity
import com.gdn.android.onestop.util.SessionManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception


@Module
class AppModule{

    @Provides
    fun provideFragment() : Fragment {
        return Fragment()
    }

    @Provides
    fun provideRetrofit(sessionManager: SessionManager, context: Context): Retrofit {
        val logger = HttpLoggingInterceptor()
        logger.level = HttpLoggingInterceptor.Level.BODY
        val clientBuilder = OkHttpClient.Builder()

        clientBuilder.addInterceptor(logger)
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
                    val intent = Intent(context, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)
                }
                response
            }
            catch (e : Exception){
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