package com.example.cointrace.ui.simulation

import android.annotation.SuppressLint
import com.example.cointrace.R
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.cointrace.models.CryptoCurrency
import com.example.cointrace.models.HistoricalDataResponse
import com.example.cointrace.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class SimulationFragment : Fragment() {

    private lateinit var resultContainer: TextView
    private lateinit var submitButton: Button
    private lateinit var cryptoSpinner: Spinner
    private lateinit var dateInput1: EditText
    private lateinit var dateInput2: EditText
    private lateinit var montant: EditText
    private lateinit var frequencySpinner: Spinner

    private var cryptoList: List<CryptoCurrency> = emptyList()  // Liste des cryptos pour garder une trace des objets

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_simulation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialisation des vues
        resultContainer = view.findViewById(R.id.result_container)
        submitButton = view.findViewById(R.id.submit_button)
        cryptoSpinner = view.findViewById(R.id.crypto_spinner)
        dateInput1 = view.findViewById(R.id.date_input1)
        dateInput2 = view.findViewById(R.id.date_input2)
        montant = view.findViewById(R.id.montant_input)
        frequencySpinner = view.findViewById(R.id.frequency_spinner)

        // Initialisation des composants
        submitButton.isEnabled = false
        setupListeners()

        // Récupérer les cryptos depuis l'API
        fetchCryptoData()

        // Configure le spinner pour la fréquence
        setupFrequencySpinner()

        // Configure les date pickers
        setupDatePickers()
    }

    private fun setupListeners() {
        val validate = {
            validateFields()
        }

        montant.addTextChangedListener { validate() }
        dateInput1.addTextChangedListener { validate() }
        dateInput2.addTextChangedListener { validate() }

        cryptoSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                validate()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        frequencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                validate()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        submitButton.setOnClickListener {
            handleSubmit()
        }
    }

    private fun validateFields() {
        val isMontantFilled = montant.text.toString().isNotEmpty()
        val isDateInput1Filled = dateInput1.text.toString().isNotEmpty()
        val isDateInput2Filled = dateInput2.text.toString().isNotEmpty()

        submitButton.isEnabled = isMontantFilled && isDateInput1Filled && isDateInput2Filled
    }

    private fun setupFrequencySpinner() {
        val adapterFrequency = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.spinner_frequency_options,
            android.R.layout.simple_spinner_item
        )
        adapterFrequency.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        frequencySpinner.adapter = adapterFrequency
    }

    private fun setupDatePickers() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        dateInput1.setOnClickListener {
            val datePicker = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                val date = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                dateInput1.setText(date)
            }, year, month, day)
            datePicker.show()
        }

        dateInput2.setOnClickListener {
            val datePicker = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                val date = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                dateInput2.setText(date)
            }, year, month, day)
            datePicker.show()
        }
    }

    private fun fetchCryptoData() {
        val call = RetrofitInstance.api.getCryptoData(currency = "eur", perPage = 15)

        call.enqueue(object : Callback<List<CryptoCurrency>> {
            override fun onResponse(
                call: Call<List<CryptoCurrency>>,
                response: Response<List<CryptoCurrency>>
            ) {
                if (response.isSuccessful) {
                    val cryptoListResponse = response.body()
                    if (!cryptoListResponse.isNullOrEmpty()) {
                        val filteredCryptoList = cryptoListResponse.filter { it.name != "Lido Staked Ether" }
                        cryptoList = filteredCryptoList // Garder cette liste pour une utilisation ultérieure

                        val cryptoNames = filteredCryptoList.map { it.name }

                        val adapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_spinner_item,
                            cryptoNames
                        )
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        cryptoSpinner.adapter = adapter
                    } else {
                        Log.e("API Error", "Aucune donnée trouvée pour les cryptos.")
                        Toast.makeText(context, "Aucune donnée trouvée", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("API Error", "Erreur API: ${response.code()} - ${response.errorBody()?.string()}")
                    Toast.makeText(context, "Erreur API : ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<CryptoCurrency>>, t: Throwable) {
                Log.e("API Error", "Erreur réseau: ${t.message}")
                Toast.makeText(context, "Erreur réseau : ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun handleSubmit() {
        val selectedCryptoName = cryptoSpinner.selectedItem.toString()
        val selectedCrypto = cryptoList.find { it.name == selectedCryptoName }
        val selectedCryptoId = selectedCrypto?.id ?: "" // Utilise l'ID de la crypto

        // Vérification que l'ID de la crypto est valide
        if (selectedCryptoId.isEmpty()) {
            Toast.makeText(context, "ID de la crypto invalide", Toast.LENGTH_SHORT).show()
            return
        }

        // Calcul de la différence de jours entre les deux dates
        val startDate = dateInput1.text.toString()
        val endDate = dateInput2.text.toString()
        val daysDifference = calculateDaysDifference(startDate, endDate)

        // Récupération de la fréquence sélectionnée
        val frequency = frequencySpinner.selectedItem.toString()

        // Calcul du nombre d'unités selon la fréquence choisie (semaine, mois, an)
        val numberOfUnits = when (frequency) {
            "Par semaine" -> (daysDifference / 7)  // Nombre de semaines
            "Par mois" -> (daysDifference / 30)  // Nombre de mois (approximatif)
            else -> daysDifference  // Par jour, donc le nombre de jours
        }

        // Ajout de logs pour vérifier les valeurs
        Log.d("SimulationFragment", "selectedCryptoId: $selectedCryptoId, daysDifference: $daysDifference, frequency: $frequency")
        Log.d("SimulationFragment", "Nombre de jours: $daysDifference, Nombre d'unités investies: $numberOfUnits")

        // Appeler la fonction pour récupérer les données historiques avec tous les paramètres nécessaires
        fetchHistoricalData(selectedCryptoId, daysDifference.toString(), montant.text.toString().toDouble(), frequency, numberOfUnits)
    }

    private fun fetchHistoricalData(cryptoId: String, days: String, montant: Double, frequency: String, numberOfUnits: Int) {
        val call = RetrofitInstance.api.getHistoricalData(cryptoId, "eur", days)

        call.enqueue(object : Callback<HistoricalDataResponse> {
            override fun onResponse(
                call: Call<HistoricalDataResponse>,
                response: Response<HistoricalDataResponse>
            ) {
                if (response.isSuccessful) {
                    val historicalData = response.body()
                    if (historicalData != null) {
                        // Ajout d'un log pour vérifier les prix avant filtrage
                        Log.d("Simulation", "Prix historique récupérés : ${historicalData.prices}")

                        val filteredPrices = when (frequency) {
                            "Par jour" -> historicalData.prices.take(days.toInt())  // Prendre uniquement les X premiers jours
                            "Par semaine" -> historicalData.prices.filterIndexed { index, _ -> index % 7 == 0 && index < days.toInt() }  // Filtrer chaque 7 jours
                            "Par mois" -> historicalData.prices.filterIndexed { index, _ -> index % 30 == 0 && index < days.toInt()}  // Filtrer chaque 30 jours
                            else -> historicalData.prices
                        }

                        // Vérifier la taille des prix filtrés
                        Log.d("Simulation", "Nombre de points filtrés : ${filteredPrices.size}")

                        calculateAndDisplayResults(filteredPrices, montant, numberOfUnits, frequency, days.toInt())
                    } else {
                        Toast.makeText(context, "Aucune donnée historique trouvée", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("API Error", "Erreur API pour les données historiques: ${response.code()} - ${response.errorBody()?.string()}")
                    Toast.makeText(context, "Erreur API pour les données historiques", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<HistoricalDataResponse>, t: Throwable) {
                Log.e("API Error", "Erreur réseau pour les données historiques : ${t.message}")
                Toast.makeText(context, "Erreur réseau pour les données historiques", Toast.LENGTH_SHORT).show()
            }
        })
    }

    @SuppressLint("DefaultLocale")
    private fun calculateAndDisplayResults(filteredPrices: List<List<Double>>, montant: Double, numberOfUnits: Int, frequency: String, daysDifference: Int) {
        var totalCrypto = 0.0
        val totalInvested = montant * numberOfUnits  // Investissement total selon la fréquence

        // Log pour afficher le montant investi total
        Log.d("Simulation", "Montant total investi : $totalInvested €")

        // Calculer le nombre total de crypto achetées pour chaque unité (jour/semaine/mois/an)
        for (pricePoint in filteredPrices) {
            val price = pricePoint[1] // Le prix de la crypto pour ce jour
            totalCrypto += montant / price // Montant investi en crypto pour ce jour
        }

        // Log pour vérifier le total de crypto acheté
        Log.d("Simulation", "Total crypto acheté : $totalCrypto")

        // Calcul de la valeur actuelle
        val currentPrice = filteredPrices.last()[1] // Prix actuel (dernier prix dans la liste)
        val totalValue = totalCrypto * currentPrice // Valeur actuelle de la crypto à ce prix

        // Log pour vérifier la valeur actuelle
        Log.d("Simulation", "Valeur actuelle : $totalValue €")

        // Calcul du gain ou de la perte
        val gain = totalValue - totalInvested

        // Log pour afficher le gain
        Log.d("Simulation", "Gain total : $gain €")

        // Affichage des résultats
        val result = """
    Montant total investi : ${String.format("%.2f", totalInvested)} €
    Valeur actuelle : ${String.format("%.2f", totalValue)} €
    Gain total : ${String.format("%.2f", gain)} €
""".trimIndent()

        resultContainer.text = result
        resultContainer.visibility = View.VISIBLE // Rendre le TextView visible
    }

    private fun calculateDaysDifference(startDate: String, endDate: String): Int {
        val formatter = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())

        try {
            // Convertir les dates en objets Date
            val start = formatter.parse(startDate)
            val end = formatter.parse(endDate)

            // Vérifier que les deux dates ne sont pas nulles avant de continuer
            if (start == null || end == null) {
                throw IllegalArgumentException("Les dates fournies ne sont pas valides.")
            }

            // Calculer la différence en jours
            val diff = end.time - start.time
            return (diff / (1000 * 60 * 60 * 24)).toInt() // Conversion en jours
        } catch (e: Exception) {
            Log.e("Date Error", "Erreur dans le format des dates : ${e.message}")
            Toast.makeText(context, "Erreur dans les dates", Toast.LENGTH_SHORT).show()
            return 0
        }
    }
}