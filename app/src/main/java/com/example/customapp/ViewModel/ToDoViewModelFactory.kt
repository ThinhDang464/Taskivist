package com.example.customapp.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.customapp.Model.ToDoRepo

class ToDoViewModelFactory(private val repo: ToDoRepo) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ToDoViewModel::class.java)) {
            return ToDoViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

