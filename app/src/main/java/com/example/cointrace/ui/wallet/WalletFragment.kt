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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cointrace.DatabaseHelper
import com.example.cointrace.R
import com.example.cointrace.adapters.TraderAdapter

class WalletFragment : Fragment() {

    private var balance: Double = 0.0
    private lateinit var dbHelper: DatabaseHelper
    private var walletId: Long = -1
    private lateinit var recyclerView: RecyclerView
    private lateinit var balanceTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_wallet, container, false)

        recyclerView = view.findViewById(R.id.traderRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        dbHelper = DatabaseHelper(requireContext())
        val sharedPreferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getLong("user_id", -1)

        // Gestion du solde
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

        balanceTextView = view.findViewById(R.id.balanceTextView)
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

        // Fonction pour gérer la vente d'une crypto
        fun createSellCallback(userId: Long): (com.example.cointrace.models.Trade) -> Unit = { trade ->
            dbHelper.deleteTrade(userId, trade)
            balance += trade.amount
            dbHelper.updateWallet(walletId, userId, balance)
            balanceTextView.text = "Balance: $balance€"
            val newTradeList = dbHelper.getTradesByUser(userId)
            recyclerView.adapter = TraderAdapter(newTradeList, createSellCallback(userId))
            Toast.makeText(requireContext(), "Crypto vendue, argent récupéré.", Toast.LENGTH_SHORT).show()
        }

        // Récupération des transactions
        val tradeList = dbHelper.getTradesByUser(userId)
        if (tradeList.isEmpty()) {
            Toast.makeText(requireContext(), "Aucune transaction trouvée.", Toast.LENGTH_SHORT).show()
        } else {
            val adapter = TraderAdapter(tradeList, createSellCallback(userId))
            recyclerView.adapter = adapter
        }

        return view
    }
}