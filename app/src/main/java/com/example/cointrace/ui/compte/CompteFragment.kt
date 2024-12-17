package com.example.cointrace.ui.compte

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.cointrace.databinding.FragmentCompteBinding

class CompteFragment : Fragment() {

    private var _binding: FragmentCompteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val compteViewModel =
            ViewModelProvider(this)[CompteViewModel::class.java]

        _binding = FragmentCompteBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Gestion du bouton de connexion
        binding.loginButton.setOnClickListener {
            val username = binding.username.text.toString()
            val password = binding.password.text.toString()

            if (username == "user" && password == "1234") {
                Toast.makeText(requireContext(), "Connexion réussie !", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Échec de la connexion !", Toast.LENGTH_SHORT).show()
            }
        }

        // Gestion du bouton d'inscription
        binding.registerButton.setOnClickListener {
            val pseudo = binding.pseudo.text.toString()
            val email = binding.email.text.toString()
            val password = binding.registerPassword.text.toString()

            if (pseudo.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                Toast.makeText(requireContext(), "Inscription réussie !", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            }
        }

        // Gestion pour basculer entre les formulaires Connexion <-> Inscription
        binding.goToRegister.setOnClickListener {
            binding.loginLayout.visibility = View.GONE
            binding.registerLayout.visibility = View.VISIBLE
        }

        binding.goToLogin.setOnClickListener {
            binding.registerLayout.visibility = View.GONE
            binding.loginLayout.visibility = View.VISIBLE
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
