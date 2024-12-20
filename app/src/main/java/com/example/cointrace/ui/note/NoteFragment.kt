package com.example.cointrace.ui.note

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.cointrace.R
import com.example.cointrace.databinding.FragmentNoteBinding
import com.google.android.material.snackbar.Snackbar

class NoteFragment : Fragment(), NotesAdapter.NoteActionListener {

    private var _binding: FragmentNoteBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var notes: ArrayList<String>
    private lateinit var adapter: NotesAdapter
    private var noteToEdit: String? = null // Stocke la note sélectionnée pour modification


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoteBinding.inflate(inflater, container, false)

        // Initialisation de SharedPreferences
        sharedPreferences = requireContext().getSharedPreferences("notes", Context.MODE_PRIVATE)

        // Initialisation des notes
        notes = ArrayList()

        // Initialisation de l'adaptateur avec ce fragment comme listener
        adapter = NotesAdapter(notes, this)

        // Configuration du RecyclerView
        binding.notesRecycler.adapter = adapter
        binding.notesRecycler.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        // Chargement des notes existantes
        loadNotes()

        binding.saveBtn.setOnClickListener {
            val note = binding.editTextNote.text.toString()
            if (note.isNotEmpty()) {
                if (noteToEdit == null) {
                    // Ajouter une nouvelle note
                    saveNote(note)
                    Snackbar.make(binding.root, "Note enregistrée avec succès !", Snackbar.LENGTH_LONG).show()
                } else {
                    // Modifier une note existante
                    updateNote(noteToEdit!!, note)
                    Snackbar.make(binding.root, "Note modifiée avec succès !", Snackbar.LENGTH_LONG).show()
                    noteToEdit = null
                    binding.saveBtn.text = "Enregistrer" // Revenir au mode ajout
                }
            } else {
                Toast.makeText(requireContext(), "Veuillez entrer une note.", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    // Méthode pour enregistrer une note
    private fun saveNote(note: String) {
        with(sharedPreferences.edit()) {
            putString(note, note)
            apply()
        }
        notes.add(note)
        adapter.notifyDataSetChanged()
        binding.editTextNote.text.clear()
    }

    // Méthode pour mettre à jour une note existante
    private fun updateNote(oldNote: String, newNote: String) {
        with(sharedPreferences.edit()) {
            remove(oldNote) // Supprimer l'ancienne note
            putString(newNote, newNote) // Ajouter la nouvelle note
            apply()
        }
        loadNotes()
        binding.editTextNote.text.clear()
    }

    // Méthode pour charger les notes existantes
    private fun loadNotes() {
        notes.clear()
        val allNotes = sharedPreferences.all
        for (entry in allNotes.entries) {
            notes.add(entry.value.toString())
        }
        adapter.notifyDataSetChanged()
    }

    // Implémentation de la méthode pour supprimer une note
    override fun onDeleteNote(note: String) {
        with(sharedPreferences.edit()) {
            remove(note)
            apply()
        }
        loadNotes()
    }

    override fun onEditNote(note: String) {
        // Affiche une boîte de dialogue pour modifier la note
        noteToEdit = note // Stocke la note en cours d'édition
        binding.editTextNote.setText(note) // Charge la note dans le champ de texte
        binding.saveBtn.text = "Modifier" // Change le texte du bouton
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
