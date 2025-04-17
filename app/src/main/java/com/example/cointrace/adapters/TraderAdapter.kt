package com.example.cointrace.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.cointrace.models.Trade
import androidx.recyclerview.widget.RecyclerView
import com.example.cointrace.R
import com.example.cointrace.models.CryptoCurrency
import com.example.cointrace.network.RetrofitInstance

class TraderAdapter(private val tradeList: List<Trade>) :
    RecyclerView.Adapter<TraderAdapter.TraderViewHolder>() {

    class TraderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cryptoNameTextView: TextView = itemView.findViewById(R.id.cryptoNameTextView)
        val amountTextView: TextView = itemView.findViewById(R.id.amountTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TraderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_trade, parent, false)
        return TraderViewHolder(view)
    }

    override fun onBindViewHolder(holder: TraderViewHolder, position: Int) {
        val trade = tradeList[position]
        holder.cryptoNameTextView.text = trade.cryptoName
        holder.amountTextView.text = "${trade.amount}€"
        holder.dateTextView.text = trade.date

        // Appel à l'API pour récupérer le prix de la crypto
        getCryptoPrice(trade.cryptoName) { cryptoPrice ->
            if (cryptoPrice != null) {
                val cryptoEquivalent = trade.amount / cryptoPrice
                holder.itemView.findViewById<TextView>(R.id.cryptoEquivalentTextView).text =
                    "≈ ${"%.6f".format(cryptoEquivalent)} ${trade.cryptoName}"
            } else {
                holder.itemView.findViewById<TextView>(R.id.cryptoEquivalentTextView).text =
                    "Prix indisponible"
            }
        }
    }

    private fun getCryptoPrice(cryptoName: String, callback: (Double?) -> Unit) {
        val apiService = RetrofitInstance.api
        val cryptoId = when (cryptoName) {
            "Bitcoin", "BTC", "Bitcoin (BTC)" -> "bitcoin"
            "Ethereum", "ETH", "Ethereum (ETH)" -> "ethereum"
            "Litecoin", "LTC", "Litecoin (LTC)" -> "litecoin"
            "Tether", "USDT", "Tether (USDT)" -> "tether"
            "BNB", "Binance Coin (BNB)" -> "binancecoin"
            "XRP", "Ripple (XRP)" -> "ripple"
            "Cardano", "ADA", "Cardano (ADA)" -> "cardano"
            "Solana", "SOL", "Solana (SOL)" -> "solana"
            "Dogecoin", "DOGE", "Dogecoin (DOGE)" -> "dogecoin"
            "Polygon", "MATIC", "Polygon (MATIC)" -> "matic-network"
            else -> null
        }

        Log.d("TraderAdapter", "CryptoName: $cryptoName, CryptoId: $cryptoId")

        if (cryptoId != null) {
            apiService.getCryptoData(cryptoId, "eur").enqueue(object : retrofit2.Callback<List<CryptoCurrency>> {
                override fun onResponse(
                    call: retrofit2.Call<List<CryptoCurrency>>,
                    response: retrofit2.Response<List<CryptoCurrency>>
                ) {
                    if (response.isSuccessful) {
                        val price = response.body()?.firstOrNull()?.current_price
                        callback(price)
                    } else {
                        Log.e("TraderAdapter", "Erreur API : ${response.code()}")
                        callback(null)
                    }
                }

                override fun onFailure(call: retrofit2.Call<List<CryptoCurrency>>, t: Throwable) {
                    Log.e("TraderAdapter", "Échec de l'appel API : ${t.message}")
                    callback(null)
                }
            })
        } else {
            Log.e("TraderAdapter", "ID de crypto introuvable pour $cryptoName")
            callback(null)
        }
    }
    override fun getItemCount(): Int = tradeList.size
}