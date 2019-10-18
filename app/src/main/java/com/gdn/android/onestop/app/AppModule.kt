package com.gdn.android.onestop.app

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.gdn.android.onestop.base.UrlConstant
import com.gdn.android.onestop.login.LoginActivity
import com.gdn.android.onestop.util.SessionManager
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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


        clientBuilder.addInterceptor { chain ->

            var request = chain.request()
            if(sessionManager.isLoggedIn){
                request = request.newBuilder()
                    .addHeader("Authorization", "Bearer "+sessionManager.user!!.token).build()
            }
            println(request)

            val response = try{
                chain.proceed(request)
            }
            catch (e : Exception){
                okhttp3.Response.Builder().request(request)
                    .protocol(Protocol.H2_PRIOR_KNOWLEDGE)
                    .code(503).message("Service unavailable!").build()
            }
            if(response.code == 403 || response.code == 401){
                sessionManager.logout()
                val intent = Intent(context, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            }

            response
        }

        clientBuilder.addInterceptor(logger)

        return Retrofit.Builder().baseUrl(UrlConstant.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(clientBuilder.build())
            .build()
    }
}