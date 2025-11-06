package com.cs407.noteapp_v2.ui

import android.annotation.SuppressLint
import android.text.TextUtils.replace
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.automirrored.outlined.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Sort
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import java.util.Calendar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteSearchBar(
    noteDB: NoteDatabase,
    userId: Int,
    viewModel: NoteListViewModel,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded: Boolean = false // TODO: from SimpleSearchBar
    val textFieldState = rememberTextFieldState()

    // milestone 1 step 7
    var searchResults: List<NoteSummary> = listOf() // TODO: milestone 1 step 7: get from NoteListViewModel

    val onChangeText: (String) -> Unit = {
        // TODO: milestone 1 step 7
    }

    Box(
        modifier // TODO: from SimpleSearchBar
    ) {
        SearchBar(
            modifier = Modifier, // TODO: from SimpleSearchBar
            inputField = {
                SearchBarDefaults.InputField(
                    modifier = Modifier.testTag(stringResource(R.string.search_input_field)),
                    query = "",  // TODO: from SimpleSearchBar
                    onQueryChange = {
                        // TODO: from SimpleSearchBar
                    },
                    onSearch = {
                        // TODO: from SimpleSearchBar
                    },
                    expanded = false, // TODO: from SimpleSearchBar
                    onExpandedChange = {  // TODO: from SimpleSearchBar
                    },
                    placeholder = {  // TODO: from SimpleSearchBar
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
            expanded = false, // TODO: from SimpleSearchBar
            onExpandedChange = {
                // TODO: from SimpleSearchBar
            },
        ) {
            Column(
                Modifier // TODO: from SimpleSearchBar
            ) {
                // TODO: from SimpleSearchBar
            }
        }
    }
}

@Composable
fun GreetingText(modifier: Modifier = Modifier, name: String = "", greeting: String = "Welcome") {
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
            imageVector = Icons.Filled.MoreVert, contentDescription = stringResource(R.string.vert_description)
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
    IconButton(onClick = onClickMenu, modifier = Modifier.testTag(stringResource(R.string.drawer_menu))) {
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

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@SuppressLint("SimpleDateFormat")
@Composable
fun NoteCard(
    noteSummary: NoteSummary, noteDB: NoteDatabase, onClick: (Int) -> Unit, onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 4.dp)
            .combinedClickable(onClick = { onClick(TODO()) }, onLongClick = {
                TODO()
            }), elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.Unspecified, // TODO: milestone 2 step 8
        )
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // milestone 1 step 3
            Text(
                text = "TODO()", fontWeight = FontWeight.Bold, modifier = Modifier.testTag(stringResource(R.string.note_title_display))
            )
            Row {
                Text(
                    text = "TODO()",
                    fontWeight = FontWeight.Thin
                )
                Spacer(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(5.dp)
                )
                Text(
                    text = "TODO()"
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

    var sortOrder: Sort = Sort.LAST_EDITED_DESC // TODO: milestone 1 step 10

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

    // TODO: milestone 1 step 1 --- Pre-install notes for testing ---

    // TODO: milestone 1 step 4-5, 8, 11
    val noteList: List<NoteSummary>

    val onSearch: (String) -> Unit = {
        // TODO: milestone 1 step 8
    }

    Scaffold(
        floatingActionButton = { /* TODO: milestone 2 step 1 */ },
        topBar = { TopBar(navOut, onClickMenu, userState, noteDB) }) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .testTag(stringResource(R.string.note_list_column)),
            verticalArrangement = Arrangement.Top,
        ) {
            // TODO: milestone 1 step 8: add the search bar here
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top
            ) { // milestone 1 step 4
                item {
                    GreetingText(name = userState.name, greeting = preferState.greeting)
                }
                // TODO: milestone 1 step 4: display all the NoteSummary in noteList with NoteCard
            }
        }
    }
}
