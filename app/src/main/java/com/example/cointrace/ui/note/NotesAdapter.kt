package com.example.cointrace.ui.note

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cointrace.R

class NotesAdapter(
    private val notes: ArrayList<String>,
    private val noteActionListener: NoteActionListener
) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    // Interface pour le clic sur le bouton de suppression et modifier
    interface NoteActionListener {
        fun onDeleteNote(note: String)
        fun onEditNote(note: String)

    }


    // Crée un nouveau ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    // Lie les données au ViewHolder
    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.noteText.text = note

        // Gestion du clic sur le bouton "Supprimer"
        holder.deleteButton.setOnClickListener {
            noteActionListener.onDeleteNote(note)
        }

        // Gérer la modification
        holder.editButton.setOnClickListener {
            noteActionListener.onEditNote(note)
        }
    }

    // Retourne le nombre total d'éléments
    override fun getItemCount(): Int = notes.size

    // ViewHolder pour représenter une note
    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val noteText: TextView = itemView.findViewById(R.id.noteText)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
        val editButton: Button = itemView.findViewById(R.id.editButton)

    }
}
