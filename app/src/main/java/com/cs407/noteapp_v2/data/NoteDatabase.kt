package com.cs407.noteapp_v2.data

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.paging.PagingSource
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Transaction
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.Update
import androidx.room.Upsert
import com.cs407.noteapp_v2.R
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Entity(
    indices = [Index(
        value = ["userUID"], unique = true
    )]
)
data class User(
    @PrimaryKey(autoGenerate = true) val userId: Int = 0, val userUID: String = ""
)

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long): Date {
        return Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date): Long {
        return date.time
    }
}

enum class Priority(val value: Int, val color: Color, val str: String) {
    NONE(-1, Color.LightGray, "Set Priority"),
    LOW(0, Color(0xFFA5D6A7), "Low"),
    MEDIUM(1, Color(0xFFFFE082), "Medium"),
    HIGH(2, Color(0xFFEF9A9A), "High");

    companion object {
        fun fromValue(value: Int?): Priority {
            val prior: Priority? = entries.find { it.value == value }
            return prior ?: NONE
        }
    }
}

@Entity
data class Note(
    @PrimaryKey(autoGenerate = true) val noteId: Int = 0,
    val noteTitle: String,
    val noteAbstract: String,
    @ColumnInfo(typeAffinity = ColumnInfo.TEXT) val noteDetail: String?,
    val notePath: String?,
    val lastEdited: Date,
    val priority: Int = -1,
    val remindDate: Date?
)

@Entity(
    primaryKeys = ["userId", "noteId"],
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["userId"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = Note::class,
        parentColumns = ["noteId"],
        childColumns = ["noteId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class UserNoteRelation(
    val userId: Int,
    val noteId: Int
)

data class NoteSummary(
    val noteId: Int,
    val noteTitle: String,
    val noteAbstract: String,
    val lastEdited: Date,
    val priority: Int?
)

@Dao
interface UserDao {
    @Query("SELECT * FROM user WHERE userUID = :uid")
    suspend fun getByUID(uid: String): User?

    @Query("SELECT * FROM user WHERE userId = :id")
    suspend fun getById(id: Int): User

    @Query(
        """SELECT * FROM User, Note, UserNoteRelation
              WHERE User.userId = :id
                AND UserNoteRelation.userId = User.userId
                AND Note.noteId = UserNoteRelation.noteId
              ORDER BY Note.lastEdited DESC"""
    )
    suspend fun getUsersWithNoteListsById(id: Int): List<NoteSummary>

    @Query(
        """SELECT * FROM User, Note, UserNoteRelation
              WHERE User.userId = :id
                AND UserNoteRelation.userId = User.userId
                AND Note.noteId = UserNoteRelation.noteId
              ORDER BY Note.lastEdited DESC"""
    )
    fun getUsersWithNoteListsByIdFlow(id: Int): Flow<List<NoteSummary>>

    @Query(
        """SELECT * FROM User, Note, UserNoteRelation
              WHERE User.userId = :id
                AND UserNoteRelation.userId = User.userId
                AND Note.noteId = UserNoteRelation.noteId
                AND (  Note.noteDetail LIKE '%' || :pattern || '%'
                    OR Note.noteTitle LIKE '%' || :pattern || '%'
                    )
              ORDER BY
                CASE WHEN :sortBy = 'noteID' AND :sort = 0 THEN Note.noteId END DESC,
                CASE WHEN :sortBy = 'noteID' AND :sort = 1 THEN Note.noteId END ASC,
                CASE WHEN :sortBy = 'noteTitle' AND :sort = 0 THEN Note.noteTitle END DESC,
                CASE WHEN :sortBy = 'noteTitle' AND :sort = 1 THEN Note.noteTitle END ASC,
                CASE WHEN :sortBy = 'lastEdited' AND :sort = 0 THEN Note.lastEdited END DESC,
                CASE WHEN :sortBy = 'lastEdited' AND :sort = 1 THEN Note.lastEdited END ASC,
                CASE WHEN :sortBy = 'priority' AND :sort = 0 THEN Note.priority END DESC,
                CASE WHEN :sortBy = 'priority' AND :sort = 1 THEN Note.priority END ASC,
                CASE WHEN :sortBy = 'remindDate' AND :sort = 0 THEN Note.remindDate END DESC,
                CASE WHEN :sortBy = 'remindDate' AND :sort = 1 THEN Note.remindDate END ASC
        """
    )
    fun getUsersWithNoteListsByIdPaged(id: Int, pattern: String="", sort: Int = 0, sortBy: String = "lastEdited"): PagingSource<Int, NoteSummary>

    @Query(
        """SELECT * FROM User, Note, UserNoteRelation
              WHERE User.userId = :id
                AND UserNoteRelation.userId = User.userId
                AND Note.noteId = UserNoteRelation.noteId
                AND (  Note.noteDetail LIKE '%' || :pattern || '%'
                    OR Note.noteTitle LIKE '%' || :pattern || '%'
                    )
              ORDER BY Note.lastEdited DESC
        """
    )
    suspend fun getUsersWithNoteListsSearch(id: Int, pattern: String): List<NoteSummary>

    @Query(
        """SELECT * FROM User, Note, UserNoteRelation
              WHERE User.userId = :id
                AND UserNoteRelation.userId = User.userId
                AND Note.noteId = UserNoteRelation.noteId
                AND (  Note.noteDetail LIKE '%' || :pattern || '%'
                    OR Note.noteTitle LIKE '%' || :pattern || '%'
                    )
              ORDER BY
                CASE WHEN :sortBy = 'noteID' AND :sort = 0 THEN Note.noteId END DESC,
                CASE WHEN :sortBy = 'noteID' AND :sort = 1 THEN Note.noteId END ASC,
                CASE WHEN :sortBy = 'noteTitle' AND :sort = 0 THEN Note.noteTitle END DESC,
                CASE WHEN :sortBy = 'noteTitle' AND :sort = 1 THEN Note.noteTitle END ASC,
                CASE WHEN :sortBy = 'lastEdited' AND :sort = 0 THEN Note.lastEdited END DESC,
                CASE WHEN :sortBy = 'lastEdited' AND :sort = 1 THEN Note.lastEdited END ASC,
                CASE WHEN :sortBy = 'priority' AND :sort = 0 THEN Note.priority END DESC,
                CASE WHEN :sortBy = 'priority' AND :sort = 1 THEN Note.priority END ASC,
                CASE WHEN :sortBy = 'remindDate' AND :sort = 0 THEN Note.remindDate END DESC,
                CASE WHEN :sortBy = 'remindDate' AND :sort = 1 THEN Note.remindDate END ASC
              LIMIT 10
        """
    )
    fun getUsersWithNoteListsSearchFlow(id: Int, pattern: String, sort: Int = 0, sortBy: String = "lastEdited"): Flow<List<NoteSummary>>

    @Insert(entity = User::class)
    suspend fun insert(user: User)
}

@Dao
interface NoteDao {
    @Query("SELECT * FROM note WHERE noteId = :id")
    suspend fun getById(id: Int): Note

    @Query("SELECT noteId FROM note WHERE rowid = :rowId")
    suspend fun getByRowId(rowId: Long): Int

    @Upsert(entity = Note::class)
    suspend fun upsert(note: Note): Long

    @Insert
    suspend fun insertRelation(userAndNote: UserNoteRelation)

    @Transaction
    suspend fun upsertNote(note: Note, userId: Int): Int {
        val rowId = upsert(note)
        val noteId = getByRowId(rowId)
        if (note.noteId == 0) {
            insertRelation(UserNoteRelation(userId, noteId))
        }
        return noteId
    }

    @Query(
        """SELECT COUNT(*) FROM User, Note, UserNoteRelation
           WHERE User.userId = :userId
                AND UserNoteRelation.userId = User.userId
                AND Note.noteId = UserNoteRelation.noteId"""
    )
    suspend fun userNoteCount(userId: Int): Int
}

@Dao
interface DeleteDao {
    @Query("DELETE FROM user WHERE userId = :userId")
    suspend fun deleteUser(userId: Int)

    @Query(
        """SELECT Note.noteId FROM User, Note, UserNoteRelation
              WHERE User.userId = :userId
                AND UserNoteRelation.userId = User.userId
                AND Note.noteId = UserNoteRelation.noteId"""
    )
    suspend fun getAllNoteIdsByUser(userId: Int): List<Int>

    @Query("DELETE FROM note WHERE noteId IN (:notesIds)")
    suspend fun deleteNotes(notesIds: List<Int>)

    @Transaction
    suspend fun delete(userId: Int) {
        deleteNotes(getAllNoteIdsByUser(userId))
        deleteUser(userId)
    }
}

@Database(entities = [User::class, Note::class, UserNoteRelation::class], version = 1)
@TypeConverters(Converters::class)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun noteDao(): NoteDao
    abstract fun deleteDao(): DeleteDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: NoteDatabase? = null

        fun getDatabase(context: Context): NoteDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteDatabase::class.java,
                    context.getString(R.string.note_database),
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}