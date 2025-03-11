package com.example.cointrace

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.cointrace.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    companion object {
        private const val STORAGE_PERMISSION_CODE = 1
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Hide the ActionBar
        supportActionBar?.hide()

        // Set up view binding for the main activity layout
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check for storage permissions
        if (!checkPermission()) {
            requestStoragePermission()
        }

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
                R.id.navigation_marché, R.id.navigation_simulation, R.id.navigation_compte, R.id.navigation_note, R.id.navigation_wallet
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
        Log.d("DatabaseInfo", "User inserted with ID: $userId")
    }

    /* Inserts an example simulation into the database */
    private fun insertExampleSimulation() {
        dbHelper.insertSimulation(
            cryptoName = "Bitcoin",
            date = "2024-06-17",
            amount = 1500.0,
            result = 200.0
        )
        Log.d("DatabaseInfo", "Example simulation inserted: Bitcoin")
    }

    /* Retrieves and logs all users stored in the database */
    private fun displayAllUsers() {
        val cursor = dbHelper.getAllUsers()
        if (cursor.moveToFirst()) {
            do {
                val email = cursor.getString(cursor.getColumnIndexOrThrow("email"))
                val pseudo = cursor.getString(cursor.getColumnIndexOrThrow("pseudo"))
                Log.d("DatabaseInfo", "User - Email: $email, Pseudo: $pseudo")
            } while (cursor.moveToNext())
        }
        cursor.close() // Close the cursor after use
    }

    /* Retrieves and logs all simulations stored in the database */
    private fun displayAllSimulations() {
        val simulationsCursor = dbHelper.getAllSimulations()
        if (simulationsCursor.moveToFirst()) {
            do {
                val cryptoName = simulationsCursor.getString(simulationsCursor.getColumnIndexOrThrow("crypto_name"))
                val amount = simulationsCursor.getDouble(simulationsCursor.getColumnIndexOrThrow("amount"))
                val result = simulationsCursor.getDouble(simulationsCursor.getColumnIndexOrThrow("result"))
                Log.d("DatabaseInfo", "$cryptoName: Invested $amount€ -> Result: $result€")
            } while (simulationsCursor.moveToNext())
        }
        simulationsCursor.close() // Close the cursor after use
    }

    /* Checks if storage permissions are already granted */
    private fun checkPermission(): Boolean {
        val writePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        return writePermission == PackageManager.PERMISSION_GRANTED &&
                readPermission == PackageManager.PERMISSION_GRANTED
    }

    /* Requests storage permissions if not granted */
    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ),
            STORAGE_PERMISSION_CODE
        )
    }

    /* Handles the result of the permission request */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("PermissionInfo", "Storage permission granted")
            } else {
                Log.d("PermissionInfo", "Storage permission denied")
            }
        }
    }
}
