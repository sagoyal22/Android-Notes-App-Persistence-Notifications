package com.cs407.noteapp_v2.ui

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Rectangle
import androidx.compose.material.icons.filled.Square
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.getSelectedDate
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.cs407.noteapp_v2.data.NoteDatabase
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.cs407.noteapp_v2.R
import com.cs407.noteapp_v2.data.Note
import com.cs407.noteapp_v2.data.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.TimeZone
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.withContext

private const val LARGE_NOTE_THRESHOLD = 1024
fun saveContent(
    userId: Int,
    noteId: Int,
    title: String,
    detail: String,
    priority: Int?,
    remindDate: Date?,
    noteDB: NoteDatabase,
    context: Context,
    coroutineScope: CoroutineScope,
    navBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    coroutineScope.launch {
        // Build the Note and upsert on IO
        withContext(Dispatchers.IO) {
            val now = Calendar.getInstance().time
            // noteAbstract: first 20 chars of first line of detail
            val firstLine = detail.lineSequence().firstOrNull().orEmpty()
            val noteAbstract = firstLine.take(20)

            // If content is small, keep it inline; else write to file.
            val isLarge = detail.length > LARGE_NOTE_THRESHOLD
            var inlineDetail: String? = if (isLarge) null else detail
            var notePath: String? = null


            val note = Note(
                noteId = noteId,
                noteTitle = title.ifBlank { "New Note" },
                noteAbstract = noteAbstract,
                noteDetail = inlineDetail,
                notePath = null,                                   // ignore per spec
                lastEdited = now,          // now
                priority = -1,                                     // ignore per spec
                remindDate = null                                  // ignore per spec
            )

            // Upsert and link to userId (Room impl handles insert vs update)
            val realId = noteDB.noteDao().upsertNote(note, userId)


            if (isLarge) {
                val safeName = "note-$userId-$realId-$now"
                    .replace(" ", "_")
                    .replace(":", "_")
                val file = File(context.filesDir, safeName)
                file.writeText(detail)
                notePath = file.absolutePath

                val updated = note.copy(noteId = realId, notePath = notePath, lastEdited = now)
                noteDB.noteDao().upsertNote(updated, userId)
            }
        }

        // Back to the list after save
        navBack()
    }
    // TODO: milestone 2 step 3
}

fun convertToUTC(date: Date): Date {
    val utcDate =
        Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    val localDate = Calendar.getInstance()
    localDate.time = date
    utcDate.set(Calendar.YEAR, localDate.get(Calendar.YEAR))
    utcDate.set(Calendar.MONTH, localDate.get(Calendar.MONTH))
    utcDate.set(Calendar.DATE, localDate.get(Calendar.DATE))

    return utcDate.time
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SimpleDateFormat")
@Composable
fun InputRemindDateChip(
    time: Date?,
    onDateSelected: (Date?, Long?) -> Unit,
    onTimeSelected: (Date?, Int?, Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDate by remember { mutableStateOf(false) }
    var showTime by remember { mutableStateOf(false) }

    // Text on the chip
    val label = remember(time) {
        if (time == null) "Select remind date"
        else SimpleDateFormat("E MMM dd HH:mm").format(time)
    }

    Box(modifier = modifier.testTag(stringResource(R.string.reminder_chip))) {
        InputChip(
            selected = time != null,
            onClick = { showDate = true },
            label = { Text(label) },
            leadingIcon = {
                Icon(
                    Icons.Filled.CalendarMonth,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                // TODO: milestone 2 step 6.
                // Put your Date Selection Chip implementation here, don't delete Box.
            },
            colors = InputChipDefaults.inputChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        )
        // ----- Date Picker -----
        if (showDate) {
            val initialUTC = convertToUTC(time ?: Calendar.getInstance().time)
            val dateState = rememberDatePickerState(initialSelectedDateMillis = initialUTC.time)

            DatePickerDialog(
                onDismissRequest = { showDate = false },
                confirmButton = {
                    TextButton(onClick = {
                        onDateSelected(time, dateState.selectedDateMillis)
                        showDate = false
                        showTime = true // open Time Picker next
                    }) { Text(stringResource(R.string.OK_button)) }
                },
                dismissButton = {
                    TextButton(onClick = { showDate = false }) {
                        Text(stringResource(R.string.cancel_button))
                    }
                }
            ) {
                DatePicker(state = dateState)
            }
        }

        if (showTime) {
            val base = time ?: Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
            }.time

            val cal = Calendar.getInstance().apply { this.time = base }

            val timeState = remember {
                TimePickerState(
                    initialHour = cal.get(Calendar.HOUR_OF_DAY),
                    initialMinute = cal.get(Calendar.MINUTE),
                    is24Hour = true
                )
            }
            AlertDialog(
                onDismissRequest = { showTime = false },
                confirmButton = {
                    TextButton(onClick = {
                        onTimeSelected(time, timeState.hour, timeState.minute)
                        showTime = false
                    }) { Text(stringResource(R.string.OK_button)) }
                },
                dismissButton = {
                    TextButton(onClick = { showTime = false }) {
                        Text(stringResource(R.string.cancel_button))
                    }
                },
                text = { TimeInput(state = timeState) }
            )
        }
    }
    }


@Composable
fun PriorityChip(modifier: Modifier = Modifier, currentPriority: Priority?,                // The selected priority (can be null)
                 onPrioritySelected: (Priority) -> Unit ) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier.testTag(stringResource(R.string.priority_chip))) {
        InputChip(
            selected = expanded,
            onClick = { expanded = !expanded },
            label = {
                Text(
                    text = currentPriority?.name ?: stringResource(R.string.priority_chip),
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            colors = InputChipDefaults.inputChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        )
        {
            Priority.values().forEach {priority ->
                DropdownMenuItem(
                    text = { Text(priority.name) },
                    onClick = {
                        onPrioritySelected(priority)
                        expanded = false
                    }
                )

            }
            }
        // TODO: milestone 2 step 5.
        // Put your Priority Chip implementation here, don't delete Box.
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteContentPage(userId: Int, noteId: Int, navBack: () -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val noteDB = NoteDatabase.getDatabase(context)
    val scope = rememberCoroutineScope()
    var selectedPriority by remember { mutableStateOf<Priority?>(null) }
    var remindDate by remember { mutableStateOf<Date?>(null) }



    var title by rememberSaveable { mutableStateOf("") }
    var detail by rememberSaveable { mutableStateOf("") }
    var detailFocused by remember { mutableStateOf(false) }
    val isNew = noteId == 0
    

    val onDateSelected: (Date?, Long?) -> Unit = { oldDate, selectedDateMillis ->
        val newCal = Calendar.getInstance()
        if (oldDate != null) newCal.time = oldDate
        if (selectedDateMillis != null) {
            val pickedUTC = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                time = Date(selectedDateMillis)
            }
            newCal.set(Calendar.YEAR, pickedUTC.get(Calendar.YEAR))
            newCal.set(Calendar.MONTH, pickedUTC.get(Calendar.MONTH))
            newCal.set(Calendar.DATE, pickedUTC.get(Calendar.DATE))
            if (oldDate == null) {
                newCal.set(Calendar.HOUR_OF_DAY, 0)
                newCal.set(Calendar.MINUTE, 0)
            }
        }
        remindDate = newCal.time
    }

    val onTimeSelected: (Date?, Int?, Int?) -> Unit = { oldDate, h, m ->
        val cal = Calendar.getInstance()
        if (oldDate != null) cal.time = oldDate else cal.time = Calendar.getInstance().time
        if (h != null && m != null) {
            cal.set(Calendar.HOUR_OF_DAY, h)
            cal.set(Calendar.MINUTE, m)
            remindDate = cal.time
        }
    }

    // TODO: milestone 2 step 2: complete all the ui-related stuff inside Scaffold
    Scaffold(
        topBar = {
            TopAppBar(
                title = { /* empty on purpose */ },
                navigationIcon = {
                    IconButton(onClick = navBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Button(
                    onClick = {
                        saveContent(
                            userId = userId,
                            noteId = noteId,
                            title = title,
                            detail = detail,
                            priority = selectedPriority?.ordinal ?: -1,
                            remindDate = null,
                            noteDB = noteDB,
                            context = context,
                            coroutineScope = scope,
                            navBack = navBack
                        )
                    },
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text(stringResource(R.string.save_button))
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            BasicTextField(
                value = title,
                onValueChange = { title = it },
                textStyle = TextStyle(
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground),
                singleLine = true,
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth()
                    .testTag(stringResource(R.string.note_title_input)),
                decorationBox = { inner ->
                    Box {
                        if (title.isBlank()) {
                            Text(
                                text = if (isNew) "New Note" else "",
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        inner()
                    }

                }
            )


            HorizontalDivider(thickness = 1.dp, modifier = Modifier.padding(10.dp))

            Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)) {
                InputRemindDateChip(
                    time = remindDate,
                    onDateSelected = onDateSelected,
                    onTimeSelected = onTimeSelected,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                );
                PriorityChip(
                    currentPriority = selectedPriority,
                    onPrioritySelected = { selectedPriority = it }
                )
            }
            // ...
            BasicTextField(
                value = detail,
                onValueChange = { detail = it },
                textStyle = TextStyle(
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Normal // ...
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground),
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth()
                    .onFocusChanged { detailFocused = it.isFocused }
                    .testTag(stringResource(R.string.note_content_input)),
                decorationBox = { inner ->
                    Box {
                        if (isNew && !detailFocused && detail.isBlank()) {
                            Text(
                                text = stringResource(R.string.note_content_placeholder), // “Write here”
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Thin
                            )
                        }
                        inner()
                    }

                }

            )
        }
        Spacer(modifier = Modifier.padding(bottom = 80.dp))
    }

}