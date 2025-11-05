package com.cs407.noteapp_v2.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UserState(
    val id: Int = 0, val name: String = "", val uid: String = ""
)

class UserViewModel : ViewModel() {
    private val _userState = MutableStateFlow(UserState())
    private val auth: FirebaseAuth = Firebase.auth
    val userState = _userState.asStateFlow()

    init {
        auth.addAuthStateListener { auth ->
            val user = auth.currentUser
            if (user == null) {
                setUser(UserState())
            }
        }
    }

    fun setUser(state: UserState) {
        _userState.update {
            state
        }
    }
}

class NoteListViewModel : ViewModel() { // milestone 1 step 1
    private var _noteListState = MutableStateFlow(listOf<NoteSummary>())
    val noteListState = _noteListState.asStateFlow()

    fun updateListFlow(state: Flow<List<NoteSummary>>) {
        // milestone 1 step 2
    }

    fun updateList(state: List<NoteSummary>) {
        _noteListState.update {
            state
        }
    }

    fun delList(pos: Int) {
        _noteListState.update { currentState ->
            val currentList = currentState.toMutableList()
            currentList.removeAt(pos)
            currentList.toList()
        }
    }
}