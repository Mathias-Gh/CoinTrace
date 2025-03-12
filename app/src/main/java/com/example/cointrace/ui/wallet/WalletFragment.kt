package com.example.cointrace.ui.wallet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.cointrace.DatabaseHelper
import com.example.cointrace.R

class WalletFragment : Fragment() {

    private var balance: Double = 0.0
    private lateinit var dbHelper: DatabaseHelper
    private var walletId: Long = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_wallet, container, false)

        dbHelper = DatabaseHelper(requireContext())
        val sharedPreferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getLong("user_id", -1)

        if (userId != -1L) {
            val cursor = dbHelper.getWalletByUser(userId)
            if (cursor.moveToFirst()) {
                walletId = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
                balance = cursor.getDouble(cursor.getColumnIndexOrThrow("balance"))
            } else {
                walletId = dbHelper.insertWallet(userId, balance)
            }
            cursor.close()
        } else {
            Toast.makeText(requireContext(), "Utilisateur non connecté.", Toast.LENGTH_SHORT).show()
        }

        val balanceTextView: TextView = view.findViewById(R.id.balanceTextView)
        val amountEditText: EditText = view.findViewById(R.id.amountEditText)
        val addButton: Button = view.findViewById(R.id.addButton)

        balanceTextView.text = "Balance: $balance€"

        addButton.setOnClickListener {
            val amountText = amountEditText.text.toString()
            if (amountText.isNotEmpty()) {
                val amount = amountText.toDouble()
                balance += amount
                balanceTextView.text = "Balance: $balance€"
                dbHelper.updateWallet(walletId, userId, balance)
                amountEditText.text.clear()
            } else {
                Toast.makeText(requireContext(), "Veuillez entrer un montant.", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}