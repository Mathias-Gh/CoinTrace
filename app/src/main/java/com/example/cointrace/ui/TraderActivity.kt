package com.example.cointrace.ui
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.cointrace.DatabaseHelper
import com.example.cointrace.R
import com.google.android.material.snackbar.Snackbar

class TraderActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private var userId: Long = -1
    private var balance: Double = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        // Enlève la barre du haut
        supportActionBar?.hide()

        setContentView(R.layout.activity_trader)
        val backButton = findViewById<Button>(R.id.button)
        backButton.setOnClickListener {
            // Redirige vers CryptoDetailActivity
            val intent = Intent(this, CryptoDetailActivity::class.java)
            startActivity(intent)
            finish() // Facultatif : termine l'activité actuelle
        }
        dbHelper = DatabaseHelper(this)
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        userId = sharedPreferences.getLong("user_id", -1)

        if (userId != -1L) {
            val cursor = dbHelper.getWalletByUser(userId)
            if (cursor.moveToFirst()) {
                balance = cursor.getDouble(cursor.getColumnIndexOrThrow("balance"))
            }
            cursor.close()
        }
        // Récupère les vues
        val amountEditText = findViewById<EditText>(R.id.amountEditText)
        val spinnerCrypto = findViewById<Spinner>(R.id.spinnerCrypto)
        val btnAccept = findViewById<Button>(R.id.btnAccept)

        // Liste de cryptos pour le spinner
        val cryptoList = listOf(
            "Bitcoin (BTC)",
            "Ethereum (ETH)",
            "Tether (USDT)",
            "BNB (BNB)",
            "XRP (XRP)",
            "Cardano (ADA)",
            "Solana (SOL)",
            "Dogecoin (DOGE)",
            "Polygon (MATIC)",
            "Litecoin (LTC)"
        )

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cryptoList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCrypto.adapter = adapter

        // Action du bouton Accepter
        btnAccept.setOnClickListener {
            val montant = amountEditText.text.toString().trim()
            val crypto = spinnerCrypto.selectedItem.toString()

            // Vérification
            if (montant.isBlank()) {
                Toast.makeText(
                    applicationContext,
                    "Veuillez entrer un montant !",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                val amount = montant.toDouble()
                if (amount > balance) {
                    Toast.makeText(applicationContext, "Solde insuffisant !", Toast.LENGTH_LONG)
                        .show()
                } else {
                    balance -= amount

                    val dateFormat = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
                    val currentDate = dateFormat.format(java.util.Date())

                    dbHelper.insertTrade(
                        userId,
                        crypto,
                        amount,
                        currentDate
                    )

                    Snackbar.make(
                        findViewById(android.R.id.content),
                        "Vous avez acheté $amount € de $crypto",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }

        }
    }
}
