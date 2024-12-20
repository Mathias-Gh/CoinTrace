package com.example.cointrace.ui.marche

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cointrace.R
import com.example.cointrace.adapters.CryptoAdapter
import com.example.cointrace.models.CryptoCurrency
import com.example.cointrace.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MarchéFragment : Fragment() {

    private lateinit var cryptoRecyclerView: RecyclerView
    private lateinit var cryptoAdapter: CryptoAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_marche, container, false)

        // Initialisation de la RecyclerView
        cryptoRecyclerView = view.findViewById(R.id.cryptoRecyclerView)
        cryptoRecyclerView.layoutManager = LinearLayoutManager(context)

        // Charger les données de l'API
        fetchCryptoData()

        return view
    }

    private fun fetchCryptoData() {
        val call = RetrofitInstance.api.getCryptoData(currency = "eur", perPage = 15)

        call.enqueue(object : Callback<List<CryptoCurrency>> {
            override fun onResponse(
                call: Call<List<CryptoCurrency>>,
                response: Response<List<CryptoCurrency>>
            ) {
                if (response.isSuccessful) {
                    val cryptoList = response.body()
                    if (cryptoList != null && cryptoList.isNotEmpty()) {
                        val filteredList = cryptoList.filter { crypto ->
                            !listOf("Lido Staked Ether", "").contains(crypto.name)
                        }
                        if (filteredList.isNotEmpty()) {
                            cryptoAdapter = CryptoAdapter(filteredList)
                            cryptoRecyclerView.adapter = cryptoAdapter
                            Log.d("API_RESPONSE", "Données filtrées : $filteredList")
                        } else {
                            Log.e("API_RESPONSE", "Aucune crypto disponible après filtrage")
                            Toast.makeText(context, "Aucune crypto disponible", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Log.e("API_RESPONSE", "Liste vide ou nulle")
                    }
                } else {
                    Log.e("API_ERROR", "Erreur API : ${response.errorBody()?.string()}")
                    Toast.makeText(context, "Erreur API", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<CryptoCurrency>>, t: Throwable) {
                Log.e("API_FAILURE", "Erreur réseau : ${t.message}")
                Toast.makeText(context, "Erreur : ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
