package com.example.todo.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/*
on definit Note dans la DB Room
ici on gere si le ischecked est cochee ou non
et il y'a un id unique genere automatiquement pour chaque note
 */
@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val text: String,
    val isChecked: Boolean,
    val title: String? = null,
    val content: String? = null
)