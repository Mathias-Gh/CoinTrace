package com.example.cointrace.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cointrace.models.CryptoCurrency
import com.example.cointrace.R

class CryptoAdapter(
    private val cryptoList: List<CryptoCurrency>,
    private val onItemClick: (CryptoCurrency) -> Unit
) : RecyclerView.Adapter<CryptoAdapter.CryptoViewHolder>() {

    class CryptoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.cryptoName)
        val priceTextView: TextView = itemView.findViewById(R.id.cryptoPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CryptoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_crypto, parent, false)
        return CryptoViewHolder(view)
    }

    override fun onBindViewHolder(holder: CryptoViewHolder, position: Int) {
        val crypto = cryptoList[position]
        holder.nameTextView.text = crypto.name
        holder.priceTextView.text = "${crypto.current_price}€"

        holder.nameTextView.setOnClickListener {
            onItemClick(crypto)
        }
    }

        override fun getItemCount(): Int {
            return cryptoList.size
        }
    }