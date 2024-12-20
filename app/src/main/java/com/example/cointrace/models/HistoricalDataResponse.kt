package com.example.cointrace.models

data class HistoricalDataResponse(
    val prices: List<List<Double>> // Liste de paires [timestamp, prix]
)