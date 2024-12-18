package com.example.cointrace.models

data class CryptoCurrency(
    val id: String,
    val symbol: String,
    val name: String,
    val current_price: Double,
    val market_cap: Long,
    val price_change_percentage_24h: Double
)