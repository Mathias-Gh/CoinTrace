package com.example.cointrace.ui.compte

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.cointrace.databinding.FragmentCompteBinding
import com.example.cointrace.DatabaseHelper
import androidx.navigation.fragment.findNavController
import com.example.cointrace.R


class CompteFragment : Fragment() {

    private var _binding: FragmentCompteBinding? = null
    private val binding get() = _binding!!

    private lateinit var compteViewModel: CompteViewModel
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCompteBinding.inflate(inflater, container, false)

        // Récupérer une instance du ViewModel
        compteViewModel = ViewModelProvider(requireActivity())[CompteViewModel::class.java]

        databaseHelper = DatabaseHelper(requireContext())

        // Vérifier si l'utilisateur est connecté via SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getLong("user_id", -1)

        if (userId != -1L) {
            // L'utilisateur est connecté
            compteViewModel.isLoggedIn = true
            compteViewModel.currentUsername = databaseHelper.getUserData(userId).use { cursor ->
                if (cursor.moveToFirst()) cursor.getString(cursor.getColumnIndexOrThrow("pseudo")) else null
            }
            showAccountScreen()
        } else {
            // L'utilisateur n'est pas connecté
            compteViewModel.isLoggedIn = false
            showLoginScreen()
        }

        // Gestion des boutons (inchangée)
        binding.goToRegister.setOnClickListener {
            showRegisterScreen()
        }

        binding.goToLogin.setOnClickListener {
            showLoginScreen()
        }

        binding.registerButton.setOnClickListener {
            val pseudo = binding.pseudo.text.toString()
            val email = binding.email.text.toString()
            val password = binding.registerPassword.text.toString()

            if (pseudo.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                val result = databaseHelper.insertUser(email, password, pseudo, "")

                if (result != -1L) {
                    val userId = databaseHelper.getUserId(pseudo, password)
                    with(sharedPreferences.edit()) {
                        putLong("user_id", userId)
                        apply()
                    }
                    compteViewModel.isLoggedIn = true
                    compteViewModel.currentUsername = pseudo
                    showAccountScreen()
                } else {
                    Toast.makeText(requireContext(), "Échec de l'inscription.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Veuillez remplir tous les champs.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.loginButton.setOnClickListener {
            val pseudo = binding.username.text.toString()
            val password = binding.password.text.toString()

            if (databaseHelper.checkUser(pseudo, password)) {
                val userId = databaseHelper.getUserId(pseudo, password)
                with(sharedPreferences.edit()) {
                    putLong("user_id", userId)
                    apply()
                }
                compteViewModel.isLoggedIn = true
                compteViewModel.currentUsername = pseudo
                showAccountScreen()
            } else {
                Toast.makeText(requireContext(), "Échec de la connexion !", Toast.LENGTH_SHORT).show()
            }
        }

        binding.logoutButton.setOnClickListener {
            compteViewModel.isLoggedIn = false
            compteViewModel.currentUsername = null
            with(sharedPreferences.edit()) {
                remove("user_id")
                apply()
            }
            showLoginScreen()
        }

        binding.modifyAccountButton.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_compte_to_modifyAccountFragment)
        }

        return binding.root
    }

    private fun showLoginScreen() {
        binding.loginLayout.visibility = View.VISIBLE
        binding.registerLayout.visibility = View.GONE
        binding.accountLayout.visibility = View.GONE
    }

    private fun showRegisterScreen() {
        binding.loginLayout.visibility = View.GONE
        binding.registerLayout.visibility = View.VISIBLE
        binding.accountLayout.visibility = View.GONE
    }

    private fun showAccountScreen() {
        binding.loginLayout.visibility = View.GONE
        binding.registerLayout.visibility = View.GONE
        binding.accountLayout.visibility = View.VISIBLE

        val username = compteViewModel.currentUsername ?: "Utilisateur inconnu"
        binding.welcomeMessage.text = "Bienvenue, $username !"
        binding.userInfo.text = "Nom d'utilisateur : $username"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
