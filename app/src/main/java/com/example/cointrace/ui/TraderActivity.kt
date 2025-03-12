package com.example.cointrace.ui
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.cointrace.R
import com.google.android.material.snackbar.Snackbar

class TraderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enlève la barre du haut
        supportActionBar?.hide()

        setContentView(R.layout.activity_trader)

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
                Toast.makeText(applicationContext, "Veuillez entrer un montant !", Toast.LENGTH_LONG).show()
            } else {
                // Affiche dans la console log
                Log.d("TRADER_ACTIVITY", "Montant saisi = $montant, Crypto choisie = $crypto")

                // Option 1 : Toast
               // Toast.makeText(applicationContext, "Vous avez choisi d'acheter $montant € de $crypto", Toast.LENGTH_LONG).show()

                // Option 2 : Snackbar (plus moderne)
                 Snackbar.make(findViewById(android.R.id.content), "Vous avez choisi d'acheter $montant € de $crypto", Snackbar.LENGTH_LONG).show()
            }
        }
    }
}
