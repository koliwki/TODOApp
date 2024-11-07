package com.example.todo.data

import kotlinx.coroutines.flow.Flow

/*
interface du repo de notes definissant les operations CRUD
pour gerer les notes
 */
interface NoteRepository{

    fun getALLNotes(): Flow<List<Note>>

    suspend fun insertNote(note: Note): Result<Unit>

    suspend fun deleteNote(note: Note): Result<Unit>

    suspend fun updateNote(note: Note): Result<Unit>
}
