package com.gdn.android.onestop.app

import androidx.fragment.app.Fragment
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
    fun provideRetrofit(): Retrofit {
//        val client : OkHttpClient = OkHttpClient()
//        client.interceptors().add(Interceptor { chain ->
//            println("intercepted")
//            chain.proceed(chain.request())
//        })
        val logger = HttpLoggingInterceptor()
        logger.level = HttpLoggingInterceptor.Level.BASIC

        val client = OkHttpClient.Builder()
            .addInterceptor(logger)
            .build()

        return Retrofit.Builder().baseUrl(UrlConstant.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }
}