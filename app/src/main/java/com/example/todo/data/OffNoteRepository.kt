package com.example.todo.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import kotlin.IllegalStateException

//cette class sert de mediateur entre DAO et NoteRepository
class OfflineNoteRepository(
    private val noteDao: NoteDao,
    private val noteApiService: NoteApiService = RetrofitHelper.noteApiService
) : NoteRepository {

    override fun getALLNotes(): Flow<List<Note>> = flow {
        emitAll(noteDao.getALLNotes())

        try {
            val todosJson = noteApiService.getAllNotes()
            val notes = todosJson.todos.map { it.toNote() }

            noteDao.insertAll(notes)
            emit(notes)
        } catch (e: Exception) {
            Log.e("OfflineNoteRepository", "Erreur lors de la récupération des notes depuis l'API: ${e.message}")
        }
    }



    override suspend fun insertNote(note: Note): Result<Unit> {
        return try {
            noteDao.insert(note)

            noteApiService.addNote(note)

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("OfflineNoteRepository", "Erreur lors de l'insertion de la note: ${e.message}")
            Result.failure(e)
        }
    }


    override suspend fun deleteNote(note: Note): Result<Unit> {
        return try {
            noteApiService.deleteNote(note.id)

            noteDao.delete(note)

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("OfflineNoteRepository", "Erreur lors de la suppression de la note: ${e.message}")
            Result.failure(e)
        }
    }



    override suspend fun updateNote(note: Note): Result<Unit> {
        return try {
            val updatedNote = note.copy(isChecked = !note.isChecked)

            Log.d("OfflineNoteRepository", "Objet note avant mise à jour: $updatedNote")

            noteApiService.updateNote(note.id, updatedNote)
            noteDao.update(updatedNote)
            Log.d("OfflineNoteRepository", "Note mise à jour localement : $updatedNote")

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("OfflineNoteRepository", "Erreur lors de la mise à jour de la note: ${e.message}")
            Result.failure(e)
        }
    }





    suspend fun deleteNoteById(noteId: Int): Result<Unit> {
        return runCatching {
            noteApiService.deleteNote(noteId)
        }
    }
}
fun TodoJson.toNote():Note{
    return Note(
        id = this.id,
        text = this.todo,
        isChecked = this.completed
    )
}