package com.example.customapp.Model

import kotlinx.coroutines.flow.Flow

//Instead of querying the databases from UI layer(Activities and viewmodels) -> query the repository which manages data retrieval from sources (Room database this case)
class ToDoRepo (private val todoDao: ToDoDao){
    //retrieves all tasks from database from DAO's method -> return a Flow that emits a list of ToDoItem that can be observed for database changes
    val allTasksASC: Flow<List<ToDoItem>> = todoDao.getAllTasksASC()
    val allTaskDSC: Flow<List<ToDoItem>> = todoDao.getAllTasksDSC()

    //accepts TodoItem as parameter and insert to database by calling insert method of DAO
    suspend fun insert(toDoItem: ToDoItem){
        todoDao.insert(toDoItem)
    }

    suspend fun delete(todoItem: ToDoItem){
        todoDao.delete(todoItem)
    }

    suspend fun update(toDoItem: ToDoItem){
        todoDao.update(toDoItem)
    }
}

//Main goal:
/*
1/ As app grows -> can add more logic around ata operations (combining data from multiple sources) in this repo
2/ scalability + testing + maintanability
 */