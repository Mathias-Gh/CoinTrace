package com.example.cointrace

import android.os.Bundle
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.cointrace.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Hide the ActionBar
        supportActionBar?.hide()

        // Set up view binding for the main activity layout
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize bottom navigation
        setupBottomNavigation()

        // Initialize the database
        dbHelper = DatabaseHelper(this)

        // Example: Insert user data
        insertExampleUser()

        // Example: Insert a simulation
        insertExampleSimulation()

        // Retrieve and display user data
        displayAllUsers()

        // Retrieve and display simulations
        displayAllSimulations()
    }

    /* Sets up the bottom navigation and navigation controller */
    private fun setupBottomNavigation() {
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_marché, R.id.navigation_simulation, R.id.navigation_compte, R.id.navigation_note
            )
        )
        navView.setupWithNavController(navController)
    }

    /* Inserts an example user into the database */
    private fun insertExampleUser() {
        val userId = dbHelper.insertUser(
            email = "user@example.com",
            password = "mypassword123",
            pseudo = "UserPseudo",
            notes = "Important notes"
        )
        Toast.makeText(this, "User inserted with ID: $userId", Toast.LENGTH_SHORT).show()
    }

    /* Inserts an example simulation into the database */
    private fun insertExampleSimulation() {
        dbHelper.insertSimulation(
            cryptoName = "Bitcoin",
            date = "2024-06-17",
            amount = 1500.0,
            result = 200.0
        )
    }

    /* Retrieves and displays all users stored in the database */
    private fun displayAllUsers() {
        val cursor = dbHelper.getAllUsers()
        if (cursor.moveToFirst()) {
            do {
                val email = cursor.getString(cursor.getColumnIndexOrThrow("email"))
                val pseudo = cursor.getString(cursor.getColumnIndexOrThrow("pseudo"))
                Toast.makeText(this, "Email: $email, Pseudo: $pseudo", Toast.LENGTH_SHORT).show()
            } while (cursor.moveToNext())
        }
    }

    /* Retrieves and displays all simulations stored in the database.*/
    private fun displayAllSimulations() {
        val simulationsCursor = dbHelper.getAllSimulations()
        if (simulationsCursor.moveToFirst()) {
            do {
                val cryptoName = simulationsCursor.getString(simulationsCursor.getColumnIndexOrThrow("crypto_name"))
                val amount = simulationsCursor.getDouble(simulationsCursor.getColumnIndexOrThrow("amount"))
                val result = simulationsCursor.getDouble(simulationsCursor.getColumnIndexOrThrow("result"))
                Toast.makeText(
                    this,
                    "$cryptoName: Invested $amount€ -> Result: $result€",
                    Toast.LENGTH_SHORT
                ).show()
            } while (simulationsCursor.moveToNext())
        }
    }
}
