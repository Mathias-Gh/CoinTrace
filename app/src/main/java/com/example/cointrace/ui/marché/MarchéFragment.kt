package com.example.cointrace.ui.marche

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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MarchéFragment : Fragment() {

    private lateinit var cryptoRecyclerView: RecyclerView
    private lateinit var cryptoAdapter: CryptoAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyStateTextView: TextView

    companion object {
        private const val TAG = "MarchéFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_marche, container, false)

        // Initialisation de la RecyclerView, ProgressBar, et TextView pour état vide
        cryptoRecyclerView = view.findViewById(R.id.cryptoRecyclerView)
        progressBar = view.findViewById(R.id.progressBar)
        emptyStateTextView = view.findViewById(R.id.emptyStateTextView) // Initialisation manquante
        cryptoRecyclerView.layoutManager = LinearLayoutManager(context)

        // Charger les données de l'API
        fetchCryptoData()

        return view
    }

    private fun fetchCryptoData() {
        progressBar.visibility = View.VISIBLE
        cryptoRecyclerView.visibility = View.GONE
        emptyStateTextView.visibility = View.GONE // Cacher le message d'état vide

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
                    showError("Erreur API : ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<List<CryptoCurrency>>, t: Throwable) {
                progressBar.visibility = View.GONE
                showError("Erreur réseau : ${t.message}")
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
            cryptoAdapter = CryptoAdapter(filteredList) { crypto ->
                // Action au clic sur la crypto, par exemple ouvrir une nouvelle activité
                val intent = Intent(context, CryptoDetailActivity::class.java)
                intent.putExtra("CRYPTO_NAME", crypto.name)
                startActivity(intent)
            }
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

    private fun showError(message: String) {
        Log.e(TAG, message)
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        emptyStateTextView.visibility = View.VISIBLE // Afficher l'état vide
    }
}
