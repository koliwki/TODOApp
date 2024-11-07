package com.example.todo.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/*
ici NoteViewModelFactory permet de crée des instances de NoteViewModel
en donnant un OfflineNoteRepository elle assure que le ViewModel
est correctement initialise avec ses dépendances
 */
class NoteViewModelFactory(private val repository: OfflineNoteRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            return NoteViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
