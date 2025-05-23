package com.example.cointrace.ui.wallet

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.cointrace.R

class WalletActivity : AppCompatActivity() {

    private var balance: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet)

        val balanceTextView: TextView = findViewById(R.id.balanceTextView)
        val addButton: Button = findViewById(R.id.addButton)

        balanceTextView.text = "Balance: $balance€"

        addButton.setOnClickListener {
            balance += 10.0 // Ajoute 10€ au solde
            balanceTextView.text = "Balance: $balance€"
        }
    }
}

