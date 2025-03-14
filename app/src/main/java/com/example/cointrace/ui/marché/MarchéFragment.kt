package com.example.cointrace.ui.marche

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cointrace.R
import com.example.cointrace.adapters.CryptoAdapter
import com.example.cointrace.models.CryptoCurrency
import com.example.cointrace.network.RetrofitInstance
import com.example.cointrace.ui.CryptoDetailActivity
import com.example.cointrace.ui.FavoritesActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MarchéFragment : Fragment() {

    private lateinit var cryptoRecyclerView: RecyclerView
    private lateinit var cryptoAdapter: CryptoAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyStateTextView: TextView

    // Liste pour stocker les favoris
    private val favoriteCryptoIds = mutableSetOf<String>()

    companion object {
        private const val TAG = "MarchéFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_marche, container, false)

        cryptoRecyclerView = view.findViewById(R.id.cryptoRecyclerView)
        progressBar = view.findViewById(R.id.progressBar)
        emptyStateTextView = view.findViewById(R.id.emptyStateTextView)

        // Bouton pour voir les favoris
        val viewFavoritesButton: View = view.findViewById(R.id.viewFavoritesButton)
        viewFavoritesButton.setOnClickListener {
            showFavorites()
        }

        cryptoRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Charger les favoris enregistrés dans SharedPreferences
        loadFavoriteCryptos()

        fetchCryptoData()

        return view
    }

    private fun fetchCryptoData() {
        progressBar.visibility = View.VISIBLE
        cryptoRecyclerView.visibility = View.GONE
        emptyStateTextView.visibility = View.GONE

        val call = RetrofitInstance.api.getCryptoData(currency = "eur", perPage = 15)

        call.enqueue(object : Callback<List<CryptoCurrency>> {
            override fun onResponse(
                call: Call<List<CryptoCurrency>>,
                response: Response<List<CryptoCurrency>>
            ) {
                progressBar.visibility = View.GONE

                if (response.isSuccessful) {
                    val cryptoList = response.body()
                    handleCryptoData(cryptoList)
                } else {
                    showError("Erreur API : ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<CryptoCurrency>>, t: Throwable) {
                progressBar.visibility = View.GONE
                showError("Erreur réseau : ${t.localizedMessage ?: "Une erreur inconnue est survenue."}")
            }
        })
    }

    private fun handleCryptoData(cryptoList: List<CryptoCurrency>?) {
        if (cryptoList.isNullOrEmpty()) {
            showError("Aucune donnée reçue.")
            return
        }

        val filteredList = filterCryptoData(cryptoList)

        if (filteredList.isEmpty()) {
            showError("Aucune crypto disponible après filtrage.")
        } else {
            cryptoAdapter = CryptoAdapter(
                cryptoList = filteredList,
                onItemClick = { crypto ->
                    val intent = Intent(requireContext(), CryptoDetailActivity::class.java)
                    intent.putExtra("cryptoId", crypto.id) // Envoi de l'ID de la crypto
                    startActivity(intent)
                },
                onFavoriteClick = { crypto ->
                    toggleFavorite(crypto)
                },
                isFavoriteCheck = { crypto ->
                    isFavorite(crypto)
                }
            )

            cryptoRecyclerView.adapter = cryptoAdapter
            cryptoRecyclerView.visibility = View.VISIBLE
            Log.d(TAG, "Données filtrées : $filteredList")
        }
    }

    private fun filterCryptoData(cryptoList: List<CryptoCurrency>): List<CryptoCurrency> {
        return cryptoList.filter { crypto ->
            !listOf("Lido Staked Ether", "").contains(crypto.name)
        }
    }

    private fun toggleFavorite(crypto: CryptoCurrency) {
        val sharedPreferences =
            requireContext().getSharedPreferences("CryptoPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        if (favoriteCryptoIds.contains(crypto.id)) {
            favoriteCryptoIds.remove(crypto.id)
            editor.putBoolean(crypto.id, false)
        } else {
            favoriteCryptoIds.add(crypto.id)
            editor.putBoolean(crypto.id, true)
        }

        // Enregistrer les favoris dans SharedPreferences
        editor.apply()

        // Mettre à jour l'affichage de la RecyclerView après modification
        cryptoAdapter.notifyDataSetChanged()
    }

    private fun isFavorite(crypto: CryptoCurrency): Boolean {
        return favoriteCryptoIds.contains(crypto.id)
    }

    private fun showError(message: String) {
        Log.e(TAG, message)
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        emptyStateTextView.visibility = View.VISIBLE
    }

    // Fonction pour naviguer vers l'écran des favoris
    private fun showFavorites() {
        val intent = Intent(requireContext(), FavoritesActivity::class.java)
        startActivity(intent)
    }

    // Charger les favoris enregistrés dans SharedPreferences
    private fun loadFavoriteCryptos() {
        val sharedPreferences =
            requireContext().getSharedPreferences("CryptoPrefs", Context.MODE_PRIVATE)
        val allKeys = sharedPreferences.all

        // Chargement des favoris depuis SharedPreferences
        for (key in allKeys.keys) {
            if (sharedPreferences.getBoolean(key, false)) {
                favoriteCryptoIds.add(key)
            }
        }
    }

}