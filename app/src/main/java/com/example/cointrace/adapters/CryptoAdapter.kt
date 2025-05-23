package com.example.cointrace.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cointrace.R
import com.example.cointrace.models.CryptoCurrency
import android.util.Log

class CryptoAdapter(
    private val cryptoList: List<CryptoCurrency>,
    private val onItemClick: (CryptoCurrency) -> Unit,
    private val onFavoriteClick: (CryptoCurrency) -> Unit,
    private val isFavoriteCheck: (CryptoCurrency) -> Boolean
) : RecyclerView.Adapter<CryptoAdapter.CryptoViewHolder>() {

    class CryptoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.cryptoName)
        val priceTextView: TextView = itemView.findViewById(R.id.cryptoPrice)
        val favoriteButton: ImageButton = itemView.findViewById(R.id.favoriteButton)
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

        holder.itemView.setOnClickListener {
            Log.d("CryptoAdapter", "ID de la crypto cliquée : ${crypto.id}")  // Ajoute ce log
            onItemClick(crypto)
        }

        updateFavoriteIcon(holder, crypto)

        holder.favoriteButton.setOnClickListener {
            onFavoriteClick(crypto)
            updateFavoriteIcon(holder, crypto)
        }
    }

    override fun getItemCount(): Int = cryptoList.size

    private fun updateFavoriteIcon(holder: CryptoViewHolder, crypto: CryptoCurrency) {
        val isFavorite = isFavoriteCheck(crypto)
        if (isFavorite) {
            holder.favoriteButton.setImageResource(R.drawable.ic_favorite) // rempli
        } else {
            holder.favoriteButton.setImageResource(R.drawable.ic_favorite_border) // vide
        }
    }
}
