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
    navBack: () -> Unit
) {
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

@SuppressLint("SimpleDateFormat")
@Composable
fun InputRemindDateChip(
    time: Date?,
    onDateSelected: (Date?, Long?) -> Unit,
    onTimeSelected: (Date?, Int?, Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.testTag(stringResource(R.string.reminder_chip))) {
        // TODO: milestone 2 step 6.
        // Put your Date Selection Chip implementation here, don't delete Box.
    }
}

@Composable
fun PriorityChip(modifier: Modifier = Modifier, /* Add parameters you want */) {
    Box(modifier = modifier.testTag(stringResource(R.string.priority_chip))) {
        // TODO: milestone 2 step 5.
        // Put your Priority Chip implementation here, don't delete Box.
    }
}

@Composable
fun NoteContentPage(userId: Int, noteId: Int, navBack: () -> Unit, modifier: Modifier = Modifier) {

    // TODO: milestone 2 step 2: complete all the ui-related stuff inside Scaffold
    Scaffold(topBar = { /* ... */ }, bottomBar = { /* ... */ }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            BasicTextField(
                value = "",
                onValueChange = {  },
                textStyle = TextStyle(
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground),
                singleLine = true,
                modifier = Modifier
                    .padding(9.dp)
                    .fillMaxWidth()
                    .testTag(stringResource(R.string.note_title_input))
            )
            HorizontalDivider(thickness = 1.dp, modifier = Modifier.padding(10.dp))
            // ...
            BasicTextField(
                value = stringResource(R.string.note_content_placeholder),
                onValueChange = { },
                textStyle = TextStyle(
                    fontSize = 20.sp,
                    color = Color.Unspecified, // ...
                    fontWeight = FontWeight.Normal // ...
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground),
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth() // ...
                    .testTag(stringResource(R.string.note_content_input)))
        }
    }
}