package com.cs407.noteapp_v2.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Sort
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.cs407.noteapp_v2.R
import com.cs407.noteapp_v2.data.NoteDatabase
import com.cs407.noteapp_v2.data.NoteListViewModel
import com.cs407.noteapp_v2.data.NoteSummary
import com.cs407.noteapp_v2.data.UserState
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import androidx.paging.compose.collectAsLazyPagingItems
import com.cs407.noteapp_v2.data.AppPreferences
import com.cs407.noteapp_v2.data.Note
import com.cs407.noteapp_v2.data.PreferenceKV
import com.cs407.noteapp_v2.data.Priority
import com.cs407.noteapp_v2.data.Sort
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Locale



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteSearchBar(
    noteDB: NoteDatabase,
    userId: Int,
    viewModel: NoteListViewModel,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val textFieldState = rememberTextFieldState()
    val scope = rememberCoroutineScope()

    // milestone 1 step 7
    val searchResults by viewModel.noteListState.collectAsState()

    val onChangeText: (String) -> Unit = { q ->
        viewModel.updateListFlow(
            noteDB.userDao().getUsersWithNoteListsSearchFlow(
                id = userId,
                pattern = q,
                sort = 0,                 // DESC
                sortBy = "lastEdited"
            )
        )
    }

    Box(
        modifier // TODO: from SimpleSearchBar
    ) {
        SearchBar(
            modifier = Modifier, // TODO: from SimpleSearchBar
            inputField = {
                SearchBarDefaults.InputField(
                    modifier = Modifier.testTag(stringResource(R.string.search_input_field)),
                    query = textFieldState.text.toString(),                         // from SimpleSearchBar

                    onQueryChange = { newText ->
                        textFieldState.edit { replace(0, length, newText) }
                        onChangeText(newText)
                        expanded = true
                    },
                    onSearch = {
                        onSearch(textFieldState.text.toString())
                        expanded = false
                    },
                    expanded = expanded, // TODO: from SimpleSearchBar
                    onExpandedChange = {
                        expanded = it // TODO: from SimpleSearchBar
                    },
                    placeholder = {
                        Text(stringResource(R.string.search_input_field)) // TODO: from SimpleSearchBar
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                val textEmpty = textFieldState.text.isEmpty()
                                textFieldState.edit {
                                    replace(0, length, "")
                                    onChangeText("")
                                    onSearch("")
                                }
                                if (expanded) expanded = false
                                else if (textEmpty) expanded = true
                            }
                        ) {
                            if (expanded || !textFieldState.text.isEmpty())
                                Icon(
                                    imageVector = Icons.Outlined.Clear,
                                    contentDescription = "Cancel Search"
                                )
                            else
                                Icon(
                                    imageVector = Icons.Outlined.Search,
                                    contentDescription = "Start Search"
                                )
                        }
                    }
                )
            },
            expanded = expanded, // TODO: from SimpleSearchBar
            onExpandedChange = {
                // TODO: from SimpleSearchBar
                expanded = it
            },
        ) {
            Column(
                Modifier // TODO: from SimpleSearchBar
            ) {
                searchResults.forEach { result ->
                    ListItem(
                        headlineContent = {
                            Column {
                                Text(result.noteTitle)
                                Text(result.noteAbstract, fontSize = 3.0.em, color = Color.Gray)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val chosen = result.noteTitle
                                textFieldState.edit { replace(0, length, chosen) }
                                onSearch(chosen)
                                expanded = false
                            }
                    )


                }
            }
        }
    }
    }

    @Composable
    fun GreetingText(
        modifier: Modifier = Modifier,
        name: String = "",
        greeting: String = "Welcome"
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.padding(10.dp)) {
            Text(
                text = "$greeting ", fontSize = 30.sp
            )
            Text(
                text = name, fontSize = 30.sp, fontStyle = FontStyle.Italic
            )
            Text(
                text = "!",
                fontSize = 30.sp,
            )
        }
    }

    @Composable
    fun DropDownList(
        navLogOut: () -> Unit,
        userState: UserState,
        noteDB: NoteDatabase,
        modifier: Modifier = Modifier
    ) {
        var expanded by remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()

        IconButton(onClick = { expanded = !expanded }) {
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = stringResource(R.string.vert_description)
            )

            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                DropdownMenuItem(leadingIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Logout,
                        contentDescription = stringResource(R.string.logout_description)
                    )
                }, text = { Text(stringResource(R.string.logout_button)) }, onClick = {
                    navLogOut()
                })
                DropdownMenuItem(
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.DeleteOutline,
                            contentDescription = stringResource(R.string.del_acc_description)
                        )
                    }, text = { Text(stringResource(R.string.del_acc_button)) }, onClick = {
                        scope.launch {
                            noteDB.deleteDao().delete(userState.id)
                        }
                        val user = Firebase.auth.currentUser
                        user?.delete()
                    }, colors = MenuDefaults.itemColors(
                        textColor = Color.Red, leadingIconColor = Color.Red
                    )
                )
            }
        }
    }

    @Composable
    fun ChangeGreetingsDialog(
        showDialog: Boolean,
        preferState: AppPreferences,
        preferKV: PreferenceKV,
        onDismissRequest: () -> Unit
    ) {
        var greeting by remember { mutableStateOf(preferState.greeting) }
        var prevGreeting by remember { mutableStateOf(preferState.greeting) }

        if (prevGreeting != preferState.greeting) {
            greeting = preferState.greeting
            prevGreeting = preferState.greeting
        }

        if (showDialog) {
            Dialog(onDismissRequest = { onDismissRequest() }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        OutlinedTextField(
                            value = greeting,
                            onValueChange = { greeting = it },
                            label = { Text(stringResource(R.string.greeting_hint)) },
                            singleLine = true,
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        TextButton(onClick = {
                            runBlocking {
                                launch {
                                    preferKV.saveGreeting(greeting)
                                }
                            }
                            onDismissRequest()
                        }) {
                            Text(stringResource(R.string.confirm_button))
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun PreferenceMenu(
        onClickMenu: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        IconButton(
            onClick = onClickMenu,
            modifier = Modifier.testTag(stringResource(R.string.drawer_menu))
        ) {
            Icon(Icons.Default.Menu, contentDescription = "Menu")
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TopBar(
        navLogOut: () -> Unit,
        onClickMenu: () -> Unit,
        userState: UserState,
        noteDB: NoteDatabase,
        modifier: Modifier = Modifier
    ) {
        TopAppBar(
            modifier = modifier,
            title = {},
            navigationIcon = {
                PreferenceMenu(onClickMenu)
            },
            actions = {
                DropDownList(navLogOut, userState, noteDB)
            },
        )
    }

@Composable
private fun priorityColor(priority: Int?): Color = when (priority) {
    Priority.HIGH.ordinal -> MaterialTheme.colorScheme.errorContainer      // red-ish
    Priority.MEDIUM.ordinal -> MaterialTheme.colorScheme.tertiaryContainer // yellow-ish
    Priority.LOW.ordinal -> MaterialTheme.colorScheme.secondaryContainer   // green-ish
    else -> MaterialTheme.colorScheme.surfaceVariant
}

    @OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
    @SuppressLint("SimpleDateFormat")
    @Composable
    fun NoteCard(
        noteSummary: NoteSummary, noteDB: NoteDatabase, onClick: (Int) -> Unit, onDelete: () -> Unit
    ) {
        val pattern = "yyyy-MM-dd HH:mm"
        val dateFormatter = SimpleDateFormat(pattern, Locale.getDefault())
        val bg = priorityColor(noteSummary.priority)
        val fg = contentColorFor(bg)


        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 4.dp)
                .combinedClickable(onClick = { onClick(noteSummary.noteId) }, onLongClick = {
                    onDelete()
                }), elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp

            ),

            colors = CardDefaults.cardColors(
                containerColor = bg,
                contentColor  = fg
            )
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                // milestone 1 step 3
                Text(
                    text = noteSummary.noteTitle,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.testTag(stringResource(R.string.note_title_display))
                )
                Row {
                    Text(
                        text = dateFormatter.format(noteSummary.lastEdited),
                        fontWeight = FontWeight.Thin
                    )
                    Spacer(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(5.dp)
                    )
                    Text(
                        text = noteSummary.noteAbstract
                    )
                }
            }
        }
        // TODO: milestone 2 step 7
    }

    @Composable
    fun NotePage(
        userState: UserState,
        modifier: Modifier = Modifier,
        onClick: (Int) -> Unit,
        navOut: () -> Unit,



        viewModel: NoteListViewModel = viewModel()
    ) {
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        var changeGreetings by remember { mutableStateOf(false) }

        val context = LocalContext.current
        val noteDB = NoteDatabase.getDatabase(context)
        val preferKV = PreferenceKV(context, userState.uid)
        val preferState by preferKV.appPreferencesFlow.collectAsState(AppPreferences())


        var sortOrder: Sort = preferState.sorting// TODO: milestone 1 step 10

        val onClickMenu: () -> Unit = {
            scope.launch {
                if (drawerState.isClosed) {
                    drawerState.open()
                } else {
                    drawerState.close()
                }
            }
        }

        ModalNavigationDrawer(
            drawerContent = {
                ModalDrawerSheet {
                    Column(Modifier.verticalScroll(rememberScrollState())) {
                        NavigationDrawerItem(
                            label = { Text(stringResource(R.string.edit_greetings_button)) },
                            selected = false,
                            icon = {
                                Icon(
                                    Icons.Outlined.Edit,
                                    contentDescription = stringResource(R.string.edit_greetings_description)
                                )
                            },
                            onClick = { changeGreetings = true; onClickMenu() },
                        )
                        HorizontalDivider(thickness = 1.dp, modifier = Modifier.padding(10.dp))

                        Sort.values().forEach { option ->
                            NavigationDrawerItem(
                                label = { Text(option.label) },       // use enum's label
                                selected = (option == preferState.sorting),     // highlight current choice
                                icon = { Icon(Icons.Outlined.Sort, contentDescription = null) },
                                onClick = {
                                    scope.launch {
                                        // persist new sort to DataStore
                                        PreferenceKV(context, userState.uid).saveSorting(option)
                                        drawerState.close()
                                    }
                                }
                            )
                        }

                        // TODO: milestone 1 step 10
                    }
                }
            },
            drawerState = drawerState
        ) {
            NoteListPage(
                preferState,
                noteDB,
                userState,
                sortOrder,
                modifier,
                onClickMenu,
                onClick,
                navOut,
                viewModel
            )
        }
        ChangeGreetingsDialog(changeGreetings, preferState, preferKV) {
            changeGreetings = false
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun NoteListPage(
        preferState: AppPreferences,
        noteDB: NoteDatabase,
        userState: UserState,
        sortOrder: Sort,
        modifier: Modifier = Modifier,
        onClickMenu: () -> Unit,
        onClick: (Int) -> Unit,
        navOut: () -> Unit,
        viewModel: NoteListViewModel = viewModel()


    ) {
        var searchPattern by rememberSaveable { mutableStateOf("") }
        val scope = rememberCoroutineScope()

        var showDeleteSheet by rememberSaveable { mutableStateOf(false) }
        var deleteNoteId by remember { mutableStateOf<Int?>(null) }
        var deleteNoteTitle by remember { mutableStateOf("") }

        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)



        // TODO: milestone 1 step 1 --- Pre-install notes for testing ---
        LaunchedEffect(Unit) {
            val countNote = noteDB.noteDao().userNoteCount(userState.id)
            if (countNote == 0 && userState.name == "large") {
                for (i in 1..1000) {
                    launch {
                        noteDB.noteDao().upsertNote(
                            Note(
                                noteTitle = "Note $i",
                                noteAbstract = "This is Note $i",
                                noteDetail = "Welcome to Note $i.",
                                notePath = null,
                                lastEdited = Calendar.getInstance().time,
                                priority = i % 3,
                                remindDate = null,
                            ), userState.id
                        )
                    }
                }
            }
        }


//step 5
        val pager = remember(searchPattern,sortOrder) {
            Pager(
                config = PagingConfig(pageSize = 20, prefetchDistance = 10)
            ) {
                // Use the PagingSource from your DAO
                noteDB.userDao().getUsersWithNoteListsByIdPaged(
                    id = userState.id,
                    pattern = searchPattern,
                    sort = sortOrder.sort,            // DESC
                    sortBy = sortOrder.sortBy
                )
            }
        }
        val noteItems = pager.flow.collectAsLazyPagingItems()

        val noteList by viewModel.noteListState.collectAsState()

        val onSearch: (String) -> Unit = {q ->
            searchPattern = q.trim()
        }

        Scaffold(
            floatingActionButton = {
                val scope = rememberCoroutineScope()
                FloatingActionButton(onClick = {
                    scope.launch {
                        val now = Calendar.getInstance().time
                        val newId = withContext(kotlinx.coroutines.Dispatchers.IO){
                            noteDB.noteDao().upsertNote(
                                Note(
                                    noteTitle   = "New Note",
                                    noteAbstract = "",
                                    noteDetail   = "",
                                    notePath     = null,
                                    lastEdited   = now,
                                    priority     = -1,
                                    remindDate   = null
                                ),
                                userState.id
                            )
                        }
                        onClick(newId)
                    }
                }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "New note")
            }
            },
            topBar = { TopBar(navOut, onClickMenu, userState, noteDB) }) { innerPadding ->
            Column(
                modifier = modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .testTag(stringResource(R.string.note_list_column)),
                verticalArrangement = Arrangement.Top,
            ) {
                // TODO: milestone 1 step 8: add the search bar here
                NoteSearchBar(
                    noteDB = noteDB,
                    userId = userState.id,
                    viewModel = viewModel,
                    onSearch = onSearch,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                )

                LazyColumn(
                    modifier = modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Top
                ) { // milestone 1 step 4
                    item {
                        GreetingText(name = userState.name, greeting = preferState.greeting)
                    }
                    // TODO: milestone 1 step 4: display all the NoteSummary in noteList with NoteCard
                    // Step 4: display the first 10 notes
                    items(noteItems.itemCount) { index ->
                        val summary = noteItems[index]
                        if (summary != null) {
                        NoteCard(
                            noteSummary = summary,
                            noteDB = noteDB,
                            onClick = { onClick(summary.noteId) },
                            onDelete = {
                                deleteNoteId = summary.noteId
                                deleteNoteTitle = summary.noteTitle
                                showDeleteSheet = true
                            }
                        )

                            }
                    }

                }

            }


    }


        if (showDeleteSheet && deleteNoteId != null) {
            ModalBottomSheet(
                sheetState = sheetState,
                onDismissRequest = { showDeleteSheet = false }
            ) {

                ListItem(
                    leadingContent = { Icon(Icons.Default.Delete, contentDescription = "Delete Note", tint = Color.Red) },
                    headlineContent = { Text(text = "Delete Note: $deleteNoteTitle",fontWeight = FontWeight.Bold ,color = Color.Red) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val id = deleteNoteId?: return@clickable
                            scope.launch {
                                withContext(kotlinx.coroutines.Dispatchers.IO) {
                                    noteDB.deleteDao().deleteNotes(listOf(id))
                                }
                                showDeleteSheet = false
                                deleteNoteId = null
                                deleteNoteTitle = ""
                            }
                        }
                )
                Spacer(Modifier.height(24.dp))
            }
        }



}




