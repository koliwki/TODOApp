package com.example.todo.data

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface NoteApiService {
    @GET("todos")
    suspend fun getAllNotes(): TodosJson

    @POST("todos")
    suspend fun addNote(@Body note: Note): Note

    @PUT("todos/{id}")
    suspend fun updateNote(@Path("id") id: Int, @Body note: Note): Response<Unit>

    @DELETE("todos/{id}")
    suspend fun deleteNote(@Path("id") id: Int): Response<Unit>

}
private const val BASE_URL = "https://dummyjson.com"
private val retrofit = Retrofit
    .Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

object RetrofitHelper{
    val noteApiService : NoteApiService by lazy {
        retrofit.create(NoteApiService::class.java)
    }
}