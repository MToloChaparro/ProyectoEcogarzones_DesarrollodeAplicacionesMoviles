package com.example.gestionecogarzones.data.remote.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // URL base para desarrollo local (10.0.2.2 apunta al localhost del host desde el emulador Android)
    // Para producción en la nube (AWS / Render / Supabase / Postgres), se reemplaza por la URL pública.
    private const val BASE_URL = "http://10.0.2.2:8080/"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}