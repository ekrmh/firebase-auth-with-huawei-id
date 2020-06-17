package com.ekrmh.firebasehuaweiauth.util

import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object NetworkSingleton{

    @Volatile
    private var retrofit: Retrofit? = null

    // My heroku rest api url
    private const val BASE_URL = "https://sleepy-island-48366.herokuapp.com/"
    private const val FIREBASE_BASE_URL = "https://identitytoolkit.googleapis.com/v1/"
    const val API_KEY = "AIzaSyCHNlag-qXibXcALzY7SOijXkPEGD7OnHk"


    @Synchronized
    fun getInstance(): Retrofit {
        retrofit ?: synchronized(this) {
            retrofit = buildRetrofit()
        }

        return retrofit!!
    }


    fun changeApiUrltoHeroku(): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()

        retrofit =  Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        return retrofit!!
    }

    fun changeApiUrltoFirebase(): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .addInterceptor { chain ->
                val original: Request = chain.request()
                val originalHttpUrl: HttpUrl = original.url()

                val url = originalHttpUrl.newBuilder()
                    .addQueryParameter("key", API_KEY)
                    .build()

                val requestBuilder: Request.Builder = original.newBuilder()
                    .url(url)

                val request: Request = requestBuilder.build()
                chain.proceed(request)
            }
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(FIREBASE_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        return retrofit!!
    }

    private fun buildRetrofit(): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }


}