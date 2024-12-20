package com.example.cointrace.network

import com.example.cointrace.models.CryptoCurrency
import com.example.cointrace.models.HistoricalDataResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("coins/markets")
    fun getCryptoData(
        @Query("vs_currency") currency: String = "eur",
        @Query("order") order: String = "market_cap_desc",
        @Query("per_page") perPage: Int = 15,
        @Query("page") page: Int = 1,
        @Query("sparkline") sparkline: Boolean = false
    ): Call<List<CryptoCurrency>>

    // Modification de l'endpoint pour les données historiques
    @GET("coins/{id}/market_chart")
    fun getHistoricalData(
        @Path("id") cryptoId: String,  // L'ID de la crypto est passé dans le chemin de l'URL
        @Query("vs_currency") currency: String,
        @Query("days") days: String
    ): Call<HistoricalDataResponse>
}