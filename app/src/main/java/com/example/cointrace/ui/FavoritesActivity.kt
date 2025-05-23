package com.example.cointrace.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cointrace.R

class FavoritesActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var favoritesListView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_favorites)

        supportActionBar?.hide()

        // Initialiser les SharedPreferences
        sharedPreferences = getSharedPreferences("CryptoPrefs", MODE_PRIVATE)

        // Récupérer les noms des cryptos favorites
        val favoriteCryptos = getFavoriteCryptos()

        val backButton = findViewById<Button>(R.id.backButton)
        backButton.setOnClickListener {
            finish() // Ferme l'activité et revient en arrière
        }

        // Si aucune crypto favorite n'est trouvée, afficher un message
        if (favoriteCryptos.isEmpty()) {
            Toast.makeText(this, "Aucun favori trouvé", Toast.LENGTH_SHORT).show()
        }

        // Initialiser la ListView pour afficher les cryptos favorites
        favoritesListView = findViewById(R.id.favoritesListView)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, favoriteCryptos)
        favoritesListView.adapter = adapter
    }

    // Fonction pour récupérer la liste des cryptos favorites à partir des SharedPreferences
    private fun getFavoriteCryptos(): List<String> {
        val keys = sharedPreferences.all.keys
        val favoriteCryptos = mutableListOf<String>()

        for (key in keys) {
            if (sharedPreferences.getBoolean(key, false)) {
                favoriteCryptos.add(key)
            }
        }
        return favoriteCryptos
    }
}
