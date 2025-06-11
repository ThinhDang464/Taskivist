package com.example.customapp.Model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.customapp.Model.ToDoItem
import kotlinx.coroutines.flow.Flow

//DAO define methods that access/interact the database

@Dao //marks the interface as DAO for room
interface ToDoDao {
    //to retrieve all tasks
    @Query("SELECT * FROM todoTable ORDER BY dueDate ASC")
    fun getAllTasksASC(): Flow<List<ToDoItem>> //return a Flow(stream of data that can change overtime) that contains a list of ToDoItem
    //Flow helps observe database changes in real-time -> if data change flow emits the updated list

    //retrieve all task descending order
    @Query("SELECT * FROM todoTable ORDER BY dueDate DESC")
    fun getAllTasksDSC(): Flow<List<ToDoItem>>

    //adding new ToDoItem to todoTable (built-in func for room)
    @Insert
    suspend fun insert(toDoItem: ToDoItem) //coroutine function(suspend keyword) -> func called within coroutine scope can be paused and resume

    @Delete
    suspend fun  delete(toDoItem: ToDoItem)

    @Update
    suspend fun update(toDoItem: ToDoItem)
}