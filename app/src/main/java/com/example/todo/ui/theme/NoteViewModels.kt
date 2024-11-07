package com.example.todo.data

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.jvm.internal.Intrinsics.Kotlin

/*
ici on gere des operations tel que la suppression ou la mise a jour
sur les notes en se connectant au repository OfflineNoteRepository
*/

class NoteViewModel(private val repository: NoteRepository) : ViewModel() {
    private val _screenState = MutableStateFlow(NotesScreenState(isLoading = true))
    val screenState = _screenState

    private val _snackbarState = MutableSharedFlow<SnackbarState>()
    val snackbarState: SharedFlow<SnackbarState> = _snackbarState

    val allNotes: Flow<List<Note>> = repository.getALLNotes().catch {
        _screenState.update {
            screenState.value.copy(
                notes = emptyList(),
                isLoading = false,
                error = it.toString()
            )
        }
        Log.e("NoteViewModel", it.toString())
        emit(emptyList())
    }

    init {
        viewModelScope.launch {
            allNotes.collect { notes ->
                _screenState.update {
                    screenState.value.copy(notes = notes, isLoading = false, error = null)
                }
            }
        }
    }

    fun insert(note: Note) = viewModelScope.launch {
        Log.d("NoteViewModel", "Insertion de la note: $note")
        _screenState.update {
            screenState.value.copy(
                isLoading = true,
                error = null
            )
        }
        kotlinx.coroutines.delay(1000)
        runCatching {
            repository.insertNote(note)
        }.onSuccess {
            _screenState.update {
                screenState.value.copy(
                    isLoading = false
                )
            }
            _snackbarState.emit(SnackbarState("Note ajoutée avec succès"))
        }
            .onFailure { throwable ->
                _screenState.update {
                    screenState.value.copy(
                        isLoading = false
                    )
                }
                _snackbarState.emit(SnackbarState("Erreur lors de l'ajout"))
            }
    }

    fun delete(note: Note) = viewModelScope.launch {
        _screenState.update { it.copy(isLoading = true, error = null) }
        kotlinx.coroutines.delay(1000)

        runCatching {
            repository.deleteNote(note)
        }.onSuccess {
            _screenState.update { currentScreenState ->
                val updatedNotes = currentScreenState.notes.filter { it.id != note.id }
                currentScreenState.copy(notes = updatedNotes, isLoading = false)
            }
            _snackbarState.emit(SnackbarState("Note supprimée avec succès"))
        }.onFailure { throwable ->
            _screenState.update { it.copy(isLoading = false) }
            _snackbarState.emit(SnackbarState("Erreur lors de la suppression : ${throwable.message}"))
        }
    }


    fun update(note: Note) = viewModelScope.launch {
        val updatedNote = note.copy(isChecked = !note.isChecked)

        _screenState.update { it.copy(isLoading = true, error = null) }

        runCatching {
            repository.updateNote(updatedNote)
        }.onSuccess {
            _screenState.update { currentScreenState ->
                val updatedNotes = currentScreenState.notes.map { existingNote ->
                    if (existingNote.id == updatedNote.id) updatedNote else existingNote
                }
                currentScreenState.copy(notes = updatedNotes, isLoading = false)
            }
            _snackbarState.emit(SnackbarState("Note mise à jour avec succès"))
        }.onFailure { throwable ->
            _screenState.update { it.copy(isLoading = false) }
            _snackbarState.emit(SnackbarState("Erreur lors de la mise à jour"))
        }
    }
}

data class NotesScreenState(
    val notes: List<Note> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

data class SnackbarState(val message: String)