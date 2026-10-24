package com.example.ultimamilla.propuesta.viewModel.Network

import android.content.Context
import com.example.ultimamilla.propuesta.viewModel.ApiService
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

object RetrofitClient {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    fun create(
        baseUrl: String,
        context: Context
    ): ApiService {

        val client = OkHttpClient.Builder()
            .addInterceptor(
                TokenInterceptor(context)
            )
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(
                json.asConverterFactory(
                    "application/json".toMediaType()
                )
            )
            .build()
            .create(ApiService::class.java)
    }
}