package com.example.cointrace.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cointrace.R

class CryptoDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        // Activer le mode edge-to-edge pour que l'interface s'ajuste aux bords
        enableEdgeToEdge()

        // Définir le layout de l'activité
        setContentView(R.layout.activity_crypto_detail)

        // Gérer les insets pour que le contenu prenne en compte les barres système
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets // Retourner les insets modifiés
        }

        // Récupérer le nom de la crypto passé par l'Intent
        val cryptoName = intent.getStringExtra("CRYPTO_NAME")

        // Afficher le nom de la crypto dans le TextView
        val cryptoNameTextView: TextView = findViewById(R.id.cryptoNameTextView)
        cryptoNameTextView.text = cryptoName ?: "Nom non disponible"
    }
}
