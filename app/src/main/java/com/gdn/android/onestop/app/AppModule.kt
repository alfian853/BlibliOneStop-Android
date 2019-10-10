package com.gdn.android.onestop.app

import androidx.fragment.app.Fragment
import com.gdn.android.onestop.util.SessionManager
import com.gdn.android.onestop.base.UrlConstant
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
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
    fun provideSessionManager() : SessionManager {
        return SessionManager()
    }

    @Provides
    fun provideRetrofit(): Retrofit {
        val logger = HttpLoggingInterceptor()
        logger.level = HttpLoggingInterceptor.Level.BODY
        val clientBuilder = OkHttpClient.Builder()

        clientBuilder.addInterceptor { chain ->
            val request = chain.request()
            val response = try{
                chain.proceed(request)
            }
            catch (e : Exception){
                okhttp3.Response.Builder().code(503).message("Service unavailable!").build()
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