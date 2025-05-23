package com.example.cointrace.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.cointrace.R
import com.example.cointrace.network.ApiService
import com.example.cointrace.network.RetrofitInstance
import com.example.cointrace.models.HistoricalDataResponse

import com.example.cointrace.ui.TraderActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.pow


class CryptoDetailActivity : AppCompatActivity() {

    private var isFavorite: Boolean = false // État du favori
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var cryptoName: String
    private lateinit var chart: LineChart
    private lateinit var apiService: ApiService
    private lateinit var cryptoId: String
    private var cryptoPrice: Double = 0.0
    private lateinit var durationSpinner: Spinner
    private var cryptoMarketCap : Double = 0.0


    private var isRateLimited = false
    private var cachedData = mutableMapOf<String, List<List<Double>>>() // variables pour gérer plusieurs durées



    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialiser l'API (important : avant fetchHistoricalData)
        apiService = RetrofitInstance.api
        // Définir le layout de l'activité
        setContentView(R.layout.activity_crypto_detail)

        supportActionBar?.hide()

        // Référence au bouton "Retour"
        val backButton: Button = findViewById(R.id.button)
        backButton.setOnClickListener {
            finish() // Termine l'activité actuelle et revient à l'écran précédent
        }

        // Activer le mode edge-to-edge pour que l'interface s'ajuste aux bords
        enableEdgeToEdge()

        // Gérer les insets pour que le contenu prenne en compte les barres système
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets // Retourner les insets modifiés
        }

        // Initialiser SharedPreferences pour persister les données
        sharedPreferences = getSharedPreferences("CryptoPrefs", MODE_PRIVATE)



        // Initialisation du graphique et du spinner
        chart = findViewById(R.id.lineChart)
        durationSpinner = findViewById(R.id.durationSpinner)

        // Récupérer le nom de la crypto passé par l'Intent
        cryptoName = intent.getStringExtra("cryptoName") ?: "Bitcoin"
        Log.d("CryptoDetailT", "Nom reçu : $cryptoName")
        cryptoId = intent.getStringExtra("cryptoId") ?: ""
        Log.d("CryptoDetail", "Crypto ID récupéré depuis l'Intent : $cryptoId")
        cryptoPrice = intent.getDoubleExtra("cryptoPrice", 0.0)

        // Vérifie si l'ID est bien récupéré
        if (cryptoId.isEmpty()) {
            Log.e("CryptoDetail", "Erreur : Aucun ID de crypto reçu !")
            Toast.makeText(this, "ID de la crypto manquant", Toast.LENGTH_SHORT).show()
            finish()  // Ferme l'activité si l'ID est manquant
            return
        }

        // Afficher le nom de la crypto dans le TextView
        val cryptoNameTextView: TextView = findViewById(R.id.cryptoNameTextView)
        cryptoNameTextView.text = cryptoName  // Assure-toi que le TextView est mis à jour

        // Afficher le prix de la crypto dans le TextView
        val cryptoPriceTextView: TextView = findViewById(R.id.cryptoPriceTextView)
        cryptoPriceTextView.text = String.format("%.2f €", cryptoPrice)  // Assure-toi que le TextView est mis à jour



        // Vérifier si cette crypto est déjà dans les favoris
        isFavorite = sharedPreferences.getBoolean(cryptoName, false)

        // Récupérer le bouton "Acheter"
        val btnBuy = findViewById<Button>(R.id.Button2)

        // Définir l'action du clic
        btnBuy.setOnClickListener {
            // Créer un Intent pour naviguer vers TraderActivity
            val intent = Intent(this, TraderActivity::class.java)
            startActivity(intent)
        }

        setupSpinner()

//        // Vérifier si les données ont déjà été récupérées récemment
//        if (cryptoId == lastFetchedCryptoId && lastFetchedData != null) {
//            Log.d("CryptoDetail", "Utilisation des données mises en cache")
//            updateChart(lastFetchedData!!) // Utiliser les données en cache
//        } else {
//            fetchHistoricalData(cryptoId) // Charger les données seulement si nécessaire
//        }
    }


    private fun setupSpinner() {
        val durations = listOf("1j", "7j", "30j", "365j", "max")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, durations)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        durationSpinner.adapter = adapter

        durationSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedDuration = durations[position]
                fetchHistoricalData(selectedDuration)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun fetchHistoricalData(duration: String) {
        val cacheKey = "$cryptoId-$duration"
        var compt = 0

        if (cachedData.containsKey(cacheKey)) {
            Log.d("CryptoDetail", "Utilisation des données en cache pour $cacheKey")
            updateChart(cachedData[cacheKey]!!)
            return
        }

        if (isRateLimited) {
            Log.w("CryptoDetail", "Requête API bloquée (limite atteinte)")
            Toast.makeText(applicationContext, "Limite API atteinte. Réessayez plus tard.", Toast.LENGTH_SHORT).show()
            return
        }

        apiService.getHistoricalData(cryptoId, "usd", duration).enqueue(object : Callback<HistoricalDataResponse> {
            override fun onResponse(
                call: Call<HistoricalDataResponse>,
                response: Response<HistoricalDataResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { historicalData ->
                        historicalData.prices?.let { prices ->
                            cachedData[cacheKey] = prices
                            updateChart(prices)
                        }
                        //donnes captilisation boursière + affichage (market_cap)
//                        historicalData.market_caps?.let { marketCaps ->
//                            // Traiter les données de market_caps si nécessaire
//                            val mc = marketCaps[0]
//                            cryptoMarketCap = mc[1]
//                            // Afficher la capitalisation de la crypto dans le TextView
//                            val cryptoMarketCapTextView: TextView = findViewById(R.id.cryptoMarketCapTextView)
//                            cryptoMarketCapTextView.text = String.format("%.2f €", cryptoMarketCap)  // Assure-toi que le TextView est mis à jour
//                            Log.d("CryptoDetailT", "Market caps: $cryptoMarketCap")
//                        }
                    }
                } else {
                    if (response.code() == 429) {
                        isRateLimited = true
                        handleRateLimitExceeded(cryptoId)
                    } else {
                        Toast.makeText(applicationContext, "Erreur API : ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<HistoricalDataResponse>, t: Throwable) {
                Toast.makeText(applicationContext, "Erreur de connexion", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Fonction pour gérer les erreurs 429 (trop de requêtes d'appel api)
    private fun handleRateLimitExceeded(cryptoId: String) {
        Log.e("CryptoDetail", "Limite d'appels API atteinte. Aucune nouvelle tentative.")
        Toast.makeText(applicationContext, "Trop de requêtes envoyées. Réessayez plus tard.", Toast.LENGTH_LONG).show()

        // Ne pas rappeler fetchHistoricalData() pour éviter de surcharger l'API
    }

    private fun updateChart(prices: List<List<Double>>) {
        if (prices.isEmpty()) {
            Log.e("CryptoDetail", "Aucune donnée de prix disponible")
            return
        }

        val entries = prices.mapNotNull {
            if (it.size == 2) {
                Entry(it[0].toFloat(), it[1].toFloat())
            } else {
                null
            }
        }

        if (entries.isEmpty()) {
            Log.e("CryptoDetail", "Aucune entrée valide pour le graphique")
            return
        }

        val dataSet = LineDataSet(entries, "Prix").apply {
            color = ColorTemplate.getHoloBlue()
            valueTextColor = Color.BLACK
            setDrawCircles(false)
            setDrawValues(false)
            lineWidth = 2f // Rend la ligne plus visible
            mode = LineDataSet.Mode.CUBIC_BEZIER // Rend la courbe plus lisse
        }

        // Appliquer les modifications à l'axe X pour supprimer les labels
        chart.xAxis.apply {
            setDrawLabels(false) // Supprime les labels
            setDrawGridLines(false) // Supprime les lignes de la grille
            position = XAxis.XAxisPosition.BOTTOM
        }

        // Appliquer les modifications à l'axe Y pour un affichage propre
        chart.axisLeft.apply {
            setDrawGridLines(true) // Active les lignes de la grille sur l'axe Y
            textColor = Color.BLACK // Texte en noir
        }
        chart.axisRight.isEnabled = false // Désactiver l'axe Y droit pour éviter la surcharge

        // Appliquer les données et rafraîchir le graphique
        chart.data = LineData(dataSet)
        chart.invalidate()
    }


}
