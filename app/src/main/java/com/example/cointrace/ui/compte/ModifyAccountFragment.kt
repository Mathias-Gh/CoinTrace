package com.example.cointrace.ui.compte

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.cointrace.DatabaseHelper
import com.example.cointrace.R
import com.example.cointrace.databinding.FragmentModifyAccountBinding

class ModifyAccountFragment : Fragment() {

    private var _binding: FragmentModifyAccountBinding? = null
    private val binding get() = _binding!!

    private lateinit var databaseHelper: DatabaseHelper
    private var userId: Long = -1
    private var currentUserPseudo: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentModifyAccountBinding.inflate(inflater, container, false)
        databaseHelper = DatabaseHelper(requireContext())

        binding.cancelButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        val sharedPreferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        userId = sharedPreferences.getLong("user_id", -1)

        if (userId != -1L) {
            val user = databaseHelper.getUserById(userId)
            binding.usernameEditText.setText(user?.pseudo)
            binding.emailEditText.setText(user?.email)
            currentUserPseudo = user?.pseudo
        } else {
            Toast.makeText(requireContext(), "Utilisateur introuvable", Toast.LENGTH_SHORT).show()
        }

        binding.saveButton.setOnClickListener {
            val newUsername = binding.usernameEditText.text.toString()
            val newEmail = binding.emailEditText.text.toString()
            val currentPassword = binding.currentPasswordEditText.text.toString()
            val newPassword = binding.newPasswordEditText.text.toString()

            // Vérifier si l'utilisateur veut changer son mot de passe
            val wantsToChangePassword = newPassword.isNotEmpty()

            val user = databaseHelper.getUserById(userId)
            val finalUsername = if (newUsername.isNotEmpty()) newUsername else user?.pseudo ?: ""
            val finalEmail = if (newEmail.isNotEmpty()) newEmail else user?.email ?: ""
            val finalPassword = if (wantsToChangePassword) newPassword else user?.password ?: ""

            if (wantsToChangePassword) {
                if (currentPassword.isEmpty()) {
                    Toast.makeText(requireContext(), "Entrez le mot de passe actuel pour en définir un nouveau.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (!databaseHelper.checkUser(currentUserPseudo ?: "", currentPassword)) {
                    Toast.makeText(requireContext(), "Mot de passe actuel incorrect.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            val updated = databaseHelper.updateUser(userId, finalEmail, finalPassword, finalUsername)
            if (updated) {
                Toast.makeText(requireContext(), "Compte mis à jour !", Toast.LENGTH_SHORT).show()
                requireActivity().onBackPressedDispatcher.onBackPressed()
            } else {
                Toast.makeText(requireContext(), "Erreur lors de la mise à jour.", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

