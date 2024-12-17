package com.example.cointrace.ui.compte

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.cointrace.databinding.FragmentCompteBinding

class CompteFragment : Fragment() {

    private var _binding: FragmentCompteBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
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

        val textView: TextView = binding.loginText
        compteViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        // Gestion du clic sur le bouton de connexion
        binding.loginButton.setOnClickListener {
            val username = binding.username.text.toString()
            val password = binding.password.text.toString()

            // Vérifiez les identifiants
            if (username == "user" && password == "1234") {
                binding.loginText.text = "Connexion réussie !"
                Toast.makeText(requireContext(), "Connexion réussie !", Toast.LENGTH_SHORT).show()
            } else {
                binding.loginText.text = "Échec de la connexion !"
                Toast.makeText(requireContext(), "Échec de la connexion !", Toast.LENGTH_SHORT).show()
            }
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}