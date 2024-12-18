package com.example.cointrace.network

import com.example.cointrace.models.CryptoCurrency
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("coins/markets")
    fun getCryptoData(
        @Query("vs_currency") currency: String = "usd",
        @Query("order") order: String = "market_cap_desc",
        @Query("per_page") perPage: Int = 10,
        @Query("page") page: Int = 1,
        @Query("sparkline") sparkline: Boolean = false
    ): Call<List<CryptoCurrency>>
}