package com.example.cointrace.ui.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
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
        val cursor = dbHelper.getWallet()
        if (cursor.moveToFirst()) {
            walletId = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
            balance = cursor.getDouble(cursor.getColumnIndexOrThrow("balance"))
        } else {
            walletId = dbHelper.insertWallet(balance)
        }
        cursor.close()

        val balanceTextView: TextView = view.findViewById(R.id.balanceTextView)
        val addButton: Button = view.findViewById(R.id.addButton)

        balanceTextView.text = "Balance: $balance€"

        addButton.setOnClickListener {
            balance += 10.0 // Add 10€ to the balance
            balanceTextView.text = "Balance: $balance€"
            dbHelper.updateWallet(walletId, balance)
        }

        return view
    }
}