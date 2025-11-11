package com.cs407.noteapp_v2

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cs407.noteapp_v2.data.UserState
import com.cs407.noteapp_v2.data.UserViewModel
import com.cs407.noteapp_v2.ui.NotePage
import com.cs407.noteapp_v2.ui.LoginPage
import com.cs407.noteapp_v2.ui.NoteContentPage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

enum class NoteScreen(@param:StringRes val title: Int) {
    Login(title = R.string.login_screen), NoteList(title = R.string.note_list_screen), NoteContent(
        title = R.string.note_content_screen
    )
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NoteApp(
    viewModel: UserViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    val userState by viewModel.userState.collectAsState()

    LaunchedEffect(userState) {
        if (userState.id == 0 || userState.name.isEmpty()) {
            navController.navigate(NoteScreen.Login.name) {
                popUpTo(navController.graph.findStartDestination().id) {
                    inclusive = true
                }
            }
        } else {
            navController.navigate(NoteScreen.NoteList.name) {
                popUpTo(navController.graph.findStartDestination().id) {
                    inclusive = true
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = NoteScreen.Login.name,
    ) {
        composable(route = NoteScreen.Login.name) {
            LoginPage(
                Modifier
            ) {
                viewModel.setUser(it)
            }
        }
        composable(route = NoteScreen.NoteList.name) {
            NotePage(userState, Modifier, {noteId ->
                navController.navigate("${NoteScreen.NoteContent.name}/$noteId")
            }, {
                Firebase.auth.signOut()
            })
        }
        // TODO: milestone 2 step 1
        composable( route = "${NoteScreen.NoteContent.name}/{noteId}",
            arguments = listOf(navArgument("noteId") { type = NavType.IntType })) {
                backStackEntry ->
            val noteId = backStackEntry.arguments?.getInt("noteId") ?: 0
            NoteContentPage(
                userId = userState.id,                          // pass current user
                noteId = noteId,
                navBack = { navController.popBackStack() }      // back to list
            )
        }


        }

    }
