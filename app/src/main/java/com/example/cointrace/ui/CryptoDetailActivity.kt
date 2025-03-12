package com.example.cointrace.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cointrace.R
import com.example.cointrace.ui.TraderActivity


class CryptoDetailActivity : AppCompatActivity() {

    private var isFavorite: Boolean = false // État du favori
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var cryptoName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        // Récupérer le nom de la crypto passé par l'Intent
        cryptoName = intent.getStringExtra("CRYPTO_NAME") ?: "Nom non disponible"

        // Afficher le nom de la crypto dans le TextView
        val cryptoNameTextView: TextView = findViewById(R.id.cryptoNameTextView)
        cryptoNameTextView.text = cryptoName

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
    }
}

