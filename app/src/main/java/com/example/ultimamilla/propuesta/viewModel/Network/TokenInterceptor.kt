package com.example.ultimamilla.propuesta.viewModel.Network

import android.content.Context
import android.util.Log
import com.example.ultimamilla.propuesta.Shared.TokenShared
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor(
    context: Context
) : Interceptor {

    private val tokenShared = TokenShared(context)

    override fun intercept(
        chain: Interceptor.Chain
    ): Response {

        val token = tokenShared.obtenerToken()

        Log.d(
            "TokenInterceptor",
            "Token disponible: ${!token.isNullOrBlank()}"
        )

        val request = chain.request()
            .newBuilder()
            .apply {

                if (!token.isNullOrBlank()) {
                    addHeader(
                        "Authorization",
                        "Bearer $token"
                    )

                    Log.d(
                        "TokenInterceptor",
                        "Authorization agregado"
                    )
                }
            }
            .build()

        return chain.proceed(request)
    }
}