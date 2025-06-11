package com.example.customapp.Model
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate


//table with 4 columns for ToDoItem

@Entity(tableName = "todoTable") //provided by Room -> marks ToDoItem data class as table in database, tableName is default name if not provided
data class ToDoItem( //data class used to hold data
    @PrimaryKey(autoGenerate = true) val id: Int = 0, //define primary key of table
    val taskName: String,
    val taskDescriptions: String,
    var isChecked: Boolean = false,
    val dueDate: String
)

//simple working flow'
/*

1/The UI (Activity or Fragment) asks the ViewModel for data.
2/The ViewModel requests the data from the Repository.
3/The Repository gets the data from the appropriate source (could be a DAO for local database access, or a Retrofit service for network calls, etc.).
4/The data flows back in the reverse order: from Repository to ViewModel, and then from ViewModel to the UI.

 */