package com.example.cointrace.ui.compte

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.cointrace.databinding.FragmentCompteBinding
import com.example.cointrace.DatabaseHelper


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

        // Vérifier si l'utilisateur est déjà connecté
        if (compteViewModel.isLoggedIn) {
            showAccountScreen()
        } else {
            showLoginScreen()
        }

        // Gestion des boutons
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
                val dbHelper = DatabaseHelper(requireContext())
                val result = dbHelper.insertUser(email, password, pseudo)


                if (result != -1L) {
                    Toast.makeText(requireContext(), "Inscription réussie !", Toast.LENGTH_SHORT).show()
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

// Bonjour blabla ooooooooooo

        binding.loginButton.setOnClickListener {
            val email = binding.username.text.toString()
            val password = binding.password.text.toString()

            if (databaseHelper.checkUser(email, password)) {
                Toast.makeText(requireContext(), "Connexion réussie !", Toast.LENGTH_SHORT).show()
                compteViewModel.isLoggedIn = true
                compteViewModel.currentUsername = email
                showAccountScreen()
            } else {
                Toast.makeText(requireContext(), "Échec de la connexion !", Toast.LENGTH_SHORT).show()
            }
        }
        binding.logoutButton.setOnClickListener {
            // Déconnexion : réinitialiser l'état de l'utilisateur
            compteViewModel.isLoggedIn = false
            compteViewModel.currentUsername = null

            // Afficher l'écran de connexion
            showLoginScreen()

            Toast.makeText(requireContext(), "Déconnexion réussie !", Toast.LENGTH_SHORT).show()
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
        binding.welcomeMessage.text = "Bienvenue, ${compteViewModel.currentUsername} !"
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
