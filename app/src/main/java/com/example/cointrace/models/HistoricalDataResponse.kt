package com.example.cointrace.models

data class HistoricalDataResponse(
    val prices: List<List<Double>>, // Liste de paires [timestamp, prix]
    val market_caps: List<List<Double>>, // Liste de paires [timestamp, market cap] pour la capitalisation de la crypto
)