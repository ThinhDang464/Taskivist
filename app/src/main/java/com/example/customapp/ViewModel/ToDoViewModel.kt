package com.example.customapp.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.customapp.Model.ToDoItem
import com.example.customapp.Model.ToDoRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
//handles data operation

//store and manage UI-related data so that data survives configuration changes
//Mediator between UI components(Activities and fragments) and the data sources (database)
//adapter would call funtion from viewmodel to deal with database -> viewmodel = takes care of database operation(remove item from database) while adapter = visual part (removing item from screen)
class ToDoViewModel (private val repo: ToDoRepo):ViewModel() //By using this repo, the ViewModel doesn't need to know about the specific database implementation, it just communicates with this repository.
{
    fun getTasksSorted(isAscending: Boolean): Flow<List<ToDoItem>> {
        return if (isAscending) {
            repo.allTasksASC
        } else {
            repo.allTaskDSC
        }
    }
    fun insert(toDoItem: ToDoItem) = viewModelScope.launch { repo.insert(toDoItem) } //any coroutine started in this scope will automatically be canceled if the ViewModel is destroyed.
    fun delete(toDoItem: ToDoItem) = viewModelScope.launch{repo.delete(toDoItem)}
    fun update(toDoItem: ToDoItem) = viewModelScope.launch { repo.update(toDoItem) }
}