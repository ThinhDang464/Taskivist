package com.example.customapp.Model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [ToDoItem::class], version = 3) //tells Room to create database with given entities, version 2 cause i altered from ver 1
@TypeConverters(LocalDateConverter::class) //used to string -> date and date -> string automatic mapping
abstract class ToDoDatabase : RoomDatabase() { //abstract class that extends RoomDatabase -> Room generate necessary codes under the hood

    abstract fun todoDao(): ToDoDao //abstract method that returns an instance of Dao -> Room generate this under the hood

    //static object associated with a class.  Define static methods or properties for a class.
    // If remove the companion object -> methods and properties inside it won't be associated with the class itself
    // but with instances of the class -> hav to create an instance of the ToDoDatabase class to access those methods and properties.
    // but ToDoDatabase is an abstract class -> cant create instances of it directly.
    companion object { //
        //getDatabase checks if there's already an instance of the database (INSTANCE). If there isn't, it creates one and returns it.
        @Volatile //keyword to ensure the variable is stored directly in main memory changes made by one thread to INSTANCE are immediately made visible to other threads.
        private var INSTANCE: ToDoDatabase? = null //INSTANCE holds a reference to a ToDoDatabase object or null if no database created

        fun getDatabase(context: Context): ToDoDatabase { //function takes a context parameter and return instance of ToDoDatabase
            return INSTANCE ?: synchronized(this) { //INSTANCE is not null then return it, null -> enters synchronized block
                //The synchronized keyword ensures that the enclosed block of code is accessed by only one thread at a time. This
                // is to prevent potential concurrency issues if multiple threads try to create a new database instance at the same time.
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ToDoDatabase::class.java,
                    "todo_database"
                )
                    .fallbackToDestructiveMigration() //if detects a version mismatch and can't find a specific migration -> delete the old database and create a new one
                    .build()
                INSTANCE = instance
                instance//return instance to getDatabase since Instance = INSTANCE next time retutn INSTANCE
            }
        }
    }
}
