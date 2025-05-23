package com.example.cointrace

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
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
    private lateinit var sharedPreferences: SharedPreferences
    private val preferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == "user_id") {
                val userId = sharedPreferences.getLong("user_id", -1)
                if (userId == -1L) {
                    setupRestrictedNavigation()
                } else {
                    setupBottomNavigation()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Hide the ActionBar
        supportActionBar?.hide()

        // Set up view binding for the main activity layout
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Vérifier si l'utilisateur est connecté
        sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener)

        // Check if the user is connected
        val userId = sharedPreferences.getLong("user_id", -1)
        if (userId == -1L) {
            setupRestrictedNavigation()
        } else {
            setupBottomNavigation()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    /* Configure la navigation restreinte pour les utilisateurs non connectés */
    private fun setupRestrictedNavigation() {
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        // Rediriger vers le fragment de compte uniquement
        navController.navigate(R.id.navigation_compte)

        // Désactiver les autres onglets
        navView.menu.findItem(R.id.navigation_marché).isEnabled = false
        navView.menu.findItem(R.id.navigation_note).isEnabled = false
        navView.menu.findItem(R.id.navigation_wallet).isEnabled = false
      
        // Retrieve and display simulations
        navView.setupWithNavController(navController)
    }

    /* Configure la navigation normale pour les utilisateurs connectés */
    private fun setupBottomNavigation() {
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_marché, R.id.navigation_compte, R.id.navigation_note, R.id.navigation_wallet
            )
        )


        navView.menu.findItem(R.id.navigation_marché).isEnabled = true
        navView.menu.findItem(R.id.navigation_note).isEnabled = true
        navView.menu.findItem(R.id.navigation_wallet).isEnabled = true
        navView.setupWithNavController(navController)
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
