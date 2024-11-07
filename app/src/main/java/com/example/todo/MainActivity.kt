package com.example.todo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.todo.data.Note
import com.example.todo.data.NoteDatabase
import com.example.todo.data.NoteViewModel
import com.example.todo.data.NoteViewModelFactory
import com.example.todo.data.OfflineNoteRepository
import com.example.todo.ui.theme.TODOTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/*
ici on initialise la DB , le repo et le viewmodel et on lance lapp
 */
class MainActivity : ComponentActivity() {
    private lateinit var noteViewModel: NoteViewModel // Changez de var à lateinit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val noteDao = NoteDatabase.getDatabase(application).noteDao()
        val repository = OfflineNoteRepository(noteDao)
        val factory = NoteViewModelFactory(repository)
        noteViewModel = ViewModelProvider(this, factory).get(NoteViewModel::class.java)
        setContent {
            TODOTheme {
                NoteApp(noteViewModel)
            }
        }
    }
}

/*
ici on gere laffichage des notes
ajout et suppression avec une interface prevue a cette effet
 */
@Composable
fun NoteApp(viewModel: NoteViewModel) {
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var noteText by rememberSaveable { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.snackbarState.collectLatest {
            snackbarHostState.showSnackbar(message = it.message)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar()
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                modifier = Modifier.padding(16.dp)
            ) {
                Text(stringResource(R.string.Add))
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        if (screenState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                items(screenState.notes, key = {
                    it.id
                }) { note ->
                    NoteItem(note = note, onDeleteClick = {
                        viewModel.delete(note)
                    },
                        onCheckedChange = { isChecked ->
                            val updatedNote = note.copy(isChecked = isChecked)
                            viewModel.update(updatedNote)
                        })
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                text = {
                    Column {
                        Text(stringResource(R.string.NoteIn))
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = noteText,
                            onValueChange = { noteText = it },
                            label = { Text(stringResource(R.string.name)) }
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (noteText.isNotBlank()) {
                                viewModel.insert(
                                    Note(
                                        text = noteText,
                                        isChecked = false,
                                    )
                                )
                                noteText = ""
                                showDialog = false
                            }
                        }
                    ) {
                        Text(stringResource(R.string.Confirmer))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text(stringResource(R.string.Cancel))
                    }
                }

            )
        }
    }
}

/*
creation de la Note item
donc du bouton supprimer de la case a cocher le texte et la card
 */
@Composable
fun NoteItem(note: Note, onDeleteClick: (Int) -> Unit, onCheckedChange: (Boolean) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = note.isChecked,
                onCheckedChange = onCheckedChange
            )
            Text(text = note.text, modifier = Modifier.weight(1f))
            Button(onClick = { onDeleteClick(note.id) }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }

        }
    }
}

//creation de la top bar (titre et fond bleu)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(modifier: Modifier = Modifier) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(R.string.NoteApp),
                style = MaterialTheme.typography.displaySmall,
            )
        },
        modifier = modifier,
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color(0xFF1976D2)
        )
    )
}

