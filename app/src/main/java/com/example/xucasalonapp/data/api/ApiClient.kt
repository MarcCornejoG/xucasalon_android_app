package com.example.xucasalonapp.data.api

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.xucasalonapp.data.SessionManager
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import java.time.LocalDate
import java.time.LocalTime
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@RequiresApi(Build.VERSION_CODES.O)
class ApiClient private constructor(sessionManager: SessionManager) {

    private val retrofit: Retrofit

    init {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val gson = GsonBuilder()
            .registerTypeAdapter(LocalDate::class.java, JsonDeserializer<LocalDate> { json, _, _ ->
                LocalDate.parse(json.asString)
            })
            .registerTypeAdapter(LocalTime::class.java, JsonDeserializer<LocalTime> { json, _, _ ->
                LocalTime.parse(json.asString)
            })
            .registerTypeAdapter(LocalDateTime::class.java, JsonDeserializer<LocalDateTime> { json, _, _ ->
                if (json.isJsonNull) null else LocalDateTime.parse(json.asString)
            })
            .setLenient()
            .create()

        val httpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(AuthInterceptor(sessionManager))
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    fun <T> createService(serviceClass: Class<T>): T = retrofit.create(serviceClass)

    companion object {
        private const val BASE_URL = "http://10.0.2.2:8080/"
        private var instance: ApiClient? = null

        fun getInstance(sessionManager: SessionManager): ApiClient {
            if (instance == null) {
                instance = ApiClient(sessionManager)
            }
            return instance!!
        }
    }
}