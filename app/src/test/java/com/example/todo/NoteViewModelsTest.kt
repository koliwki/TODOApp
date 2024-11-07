package com.example.todo

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.todo.data.Note
import com.example.todo.data.NoteRepository
import com.example.todo.data.NoteViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class NoteViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var mockRepository: NoteRepository
    private lateinit var viewModel: NoteViewModel
    private val testDispatcher = StandardTestDispatcher()
    private val notes: MutableSharedFlow<List<Note>> = MutableSharedFlow()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockRepository = mockk()
        every { mockRepository.getALLNotes() } returns notes
        viewModel = NoteViewModel(mockRepository)


    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

//    @Test
//    fun insertNoteSuccess() = runTest(testDispatcher) {
//        val note = listOf(
//            Note(
//            id = 1,
//            text = "Test Text",
//            isChecked = false,
//            title = "Test Note",
//            content = "Content of the test note")
//        )
//
//
//        viewModel.allNotes.test {
//            val insertNotesEmpty = awaitItem()
//            advanceUntilIdle()
//            assertEquals(0,insertNotesEmpty)
//
//            coEvery { mockRepository.insertNote(note) } returns Result.success(Unit)
//
//            viewModel.insert(note)
//
//            notes.emit(listOf(note))
//
//
//            notes.emit(note)
//            val insertNotes = awaitItem()
//            advanceUntilIdle()
//            assertEquals(1, insertNotes.size)
//            assertEquals("Test Note", insertNotes[0].title)
//
//            cancelAndIgnoreRemainingEvents()
//        }

 //   }
//    @Test
//    fun deleteNoteSuccess() = runTest {
//        val note = Note(
//            id = 1,
//            text = "Test Text",
//            isChecked = false,
//            title = "Test Note",
//            content = "Content of the test note"
//        )
//
//        viewModel.delete(note).test {}
//    }

    @Test
    fun getAllNotesReturnsNotes() = runTest {
        println("on est la ou pas heehaw?")
        val notesList = listOf(
            Note(
                id = 1,
                text = "Text 1",
                isChecked = false,
                title = "Note 1",
                content = "Content 1"
            ),
            Note(id = 2, text = "Text 2", isChecked = true, title = "Note 2", content = "Content 2")
        )
        println("Mock du repository : $notesList")

        viewModel.allNotes.test {
            notes.emit(emptyList())
            println("kook ok")
            val collectedNotesEmpty = awaitItem()
            advanceUntilIdle()
            assertEquals(collectedNotesEmpty.isEmpty(),true)

            notes.emit(notesList)

            val collectedNotes = awaitItem()
            advanceUntilIdle()
            println("Notes collectées: $collectedNotes")


            assertEquals(2, collectedNotes.size)
            assertEquals("Note 1", collectedNotes[0].title)
            assertEquals("Note 2", collectedNotes[1].title)

            cancelAndIgnoreRemainingEvents()
        }
    }
}

//import androidx.arch.core.executor.testing.InstantTaskExecutorRule
//import com.example.todo.data.Note
//import com.example.todo.data.NoteRepository
//import com.example.todo.data.NoteViewModel
//import io.mockk.coEvery
//import io.mockk.mockk
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.flow.flowOf
//import kotlinx.coroutines.test.StandardTestDispatcher
//import kotlinx.coroutines.test.runTest
//import kotlinx.coroutines.test.setMain
//import org.junit.Assert.assertEquals
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test

//@ExperimentalCoroutinesApi
//class NoteViewModelTest {
//
//    @get:Rule
//    val instantExecutorRule = InstantTaskExecutorRule()
//
//    private val dispatcher = StandardTestDispatcher()
//    private lateinit var viewModel: NoteViewModel
//    private val repository: NoteRepository = mockk(relaxed = true)
//
//    @Before
//    fun setUp() {
//        Dispatchers.setMain(dispatcher)
//        coEvery { repository.getALLNotes() } returns flowOf(listOf())
//        viewModel = NoteViewModel(repository)
//    }
//
//    @Test
//    fun `getAllNotes should update state with notes from repository`() = runTest {
//        val notes = listOf(
//            Note(1, "Note 1", isChecked = false),
//            Note(2, "Note 2", isChecked = true)
//        )
//        coEvery { repository.getALLNotes() } returns flowOf(notes)
//
//        viewModel = NoteViewModel(repository)
//
//        dispatcher.scheduler.advanceUntilIdle()
//        assertEquals(notes, viewModel.screenState.value.notes)
//    }
//
//
//}
