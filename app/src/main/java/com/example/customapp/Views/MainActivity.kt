package com.example.customapp.Views
import android.app.DatePickerDialog
import android.content.Intent
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.LocalDate
import java.util.Calendar
import androidx.appcompat.widget.SearchView
import com.example.customapp.R
import com.example.customapp.Model.ToDoDao
import com.example.customapp.Model.ToDoDatabase
import com.example.customapp.Model.ToDoItem
import com.example.customapp.Model.ToDoRepo
import com.example.customapp.ViewModel.ToDoViewModel
import com.example.customapp.ViewModel.ToDoViewModelFactory

//handles UI interaction
class MainActivity : AppCompatActivity() {
    private lateinit var database: ToDoDatabase
    private lateinit var dao: ToDoDao
    private lateinit var repo: ToDoRepo
    private lateinit var viewModel: ToDoViewModel
    private lateinit var adapter: ToDoAdapter
    private var isSortedAscending = true
    private var allTasks: List<ToDoItem> = listOf()


    //setting options menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)

        val searchItem = menu?.findItem(R.id.action_search) //retrieve menu item
        val searchView = searchItem?.actionView as SearchView
        //listener for user input events
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean { //called when user submit a search query by tappign search on keyboard
                return false //indicate event has not been handled -> method does nothing
            //if set to true the no other provessing will happen
            }

            override fun onQueryTextChange(newText: String?): Boolean { //when user types
                filterTasks(newText.orEmpty())// or empty returns empty string if newText is null
                return true
            }
        })
        searchView.setOnCloseListener {
            updateTasks() // Restore original list when search is closed
            true
        }

        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setting mah awesome splash art
        Thread.sleep(2000)
        installSplashScreen()
        setContentView(R.layout.activity_main)
        //display toolbar
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        //Initialize database
        database = ToDoDatabase.getDatabase(this) //val database = ToDoDatabase() this would be incorrect since abstract -> we using method companion to retrieve instance of database or create one if no exist
        dao = database.todoDao()
        repo = ToDoRepo(dao)
        val viewModelFactory = ToDoViewModelFactory(repo)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ToDoViewModel::class.java)

        //set up the recyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        //adapter
            adapter = ToDoAdapter { item ->
            // Handle the delete action here
            viewModel.delete(item)
            Toast.makeText(this, "Task finished", Toast.LENGTH_SHORT).show()
        }
        //handle onclick update todos
        adapter.onItemClicked = {toDoItem -> showEditTaskDialog(toDoItem) }
        recyclerView.adapter = adapter

        //initial list of todo items:
        updateTasks()
        //set up onclick for the FAB
        val fab: FloatingActionButton = findViewById(R.id.addTaskButton)
        fab.setOnClickListener {
            showAddTaskDialog()
        }

        //set up intent for about us activity:
        val aboutUs = findViewById<TextView>(R.id.aboutUsTextView)
        aboutUs.setOnClickListener{
            val intent = Intent(this, AboutUsActivity::class.java)
            startActivity(intent)
        }
    }

    // Handle menu item selections
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort -> {
                // Toggle the sort order and update tasks based on the new order
                isSortedAscending = !isSortedAscending
                updateTasks()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    //The observe function is designed to watch for changes in LiveData. When the data in the LiveData changes, the function provided to observe gets called.
    // The current (or new) value of the LiveData is automatically passed as an argument to this function (tasks).
    private fun updateTasks() {
        viewModel.getTasksSorted(isSortedAscending).asLiveData().observe(this) { tasks ->
            allTasks = tasks//store all current task for filtering
            adapter.submitList(tasks)//give listadapter a new list of data -> work with diffulti to see which were added or changed -> smooth
        }
    }

    //used for search function
    private fun filterTasks(query: String) {
        val filteredTasks = allTasks.filter {
            it.taskName.contains(query, ignoreCase = true) || //text appers in name
                    it.taskDescriptions.contains(query, ignoreCase = true) || //text appears in descrip
                    it.dueDate.contains(query, ignoreCase = true)//text appers in due date
        }
        adapter.submitList(filteredTasks)
    }

    //add task dialog
    private fun showAddTaskDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_task, null)
        val taskNameEditText = dialogView.findViewById<EditText>(R.id.editTextTaskName)
        val descriptionEditText = dialogView.findViewById<EditText>(R.id.editTextDescription)
        val datePickerButton = dialogView.findViewById<Button>(R.id.datePickerButton)

        //date select
        var selectedDate = LocalDate.now() // default today's date if no choose
        datePickerButton.setOnClickListener{
            val calendar = Calendar.getInstance() //calendar object representing current date and time
            val year = calendar.get(Calendar.YEAR) //extract y m and d from canlendar object
            val month= calendar.get(Calendar.MONTH) //0 based, 0 = jan
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                    selectedDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDayOfMonth)
                    datePickerButton.text = selectedDate.toString()
                },
                year, //initial dialog for choosing date is currentdate
                month,
                day
            )
            datePickerDialog.datePicker.minDate = calendar.timeInMillis
            datePickerDialog.show()
        }

        AlertDialog.Builder(this)
            .setTitle("Add Task")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val taskName = taskNameEditText.text.toString()
                val description = descriptionEditText.text.toString()
                // Validation
                if (taskName.isBlank()) {
                    Toast.makeText(this, "Task name cannot be empty", Toast.LENGTH_LONG).show()
                    return@setPositiveButton
                }
                if (taskName.length > 100) {
                    Toast.makeText(this, "Task name must be less than 100 characters", Toast.LENGTH_LONG).show()
                    return@setPositiveButton
                }
                if (description.length > 300) {
                    Toast.makeText(this, "Description must be less than 300 characters", Toast.LENGTH_LONG).show()
                    return@setPositiveButton
                }
                val toDoItem = ToDoItem(taskName = taskName, taskDescriptions = description, dueDate = selectedDate.toString())
                viewModel.insert(toDoItem)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    //update task dialog:
    private fun showEditTaskDialog(item: ToDoItem) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_task, null)
        val taskNameEditText = dialogView.findViewById<EditText>(R.id.editTextTaskName2)
        val descriptionEditText = dialogView.findViewById<EditText>(R.id.editTextDescription2)
        val datepickerButton = dialogView.findViewById<Button>(R.id.datePickerButton2)
        // Populate fields with current todos data :))
        taskNameEditText.setText(item.taskName)
        descriptionEditText.setText(item.taskDescriptions)
        datepickerButton.text = item.dueDate
        //calendar dialog:
        //date select
        var selectedDate = LocalDate.parse(item.dueDate) // default today's date if no choose
        datepickerButton.setOnClickListener{
            val calendar = Calendar.getInstance() //calendar object representing current date and time
            val year = calendar.get(Calendar.YEAR) //extract y m and d from canlendar object
            val month= calendar.get(Calendar.MONTH) //0 based, 0 = jan
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                    selectedDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDayOfMonth)
                    datepickerButton.text = selectedDate.toString()
                },
                year, //initial dialog for choosing date is currentdate
                month,
                day
            )
            datePickerDialog.datePicker.minDate = calendar.timeInMillis
            datePickerDialog.show()
        }
        AlertDialog.Builder(this)
            .setTitle("Edit Task")
            .setView(dialogView)
            .setPositiveButton("Update") { _, _ ->
                // Get updated data from user
                val updatedTaskName = taskNameEditText.text.toString()
                val updatedDescription = descriptionEditText.text.toString()
                val updatedDueDate = selectedDate.toString()
                // Validation
                if (updatedTaskName.isBlank()) {
                    Toast.makeText(this, "Task name cannot be empty", Toast.LENGTH_LONG).show()
                    return@setPositiveButton
                }
                if (updatedTaskName.length > 100) {
                    Toast.makeText(this, "Task name must be less than 100 characters", Toast.LENGTH_LONG).show()
                    return@setPositiveButton
                }
                if (updatedDescription.length > 300) {
                    Toast.makeText(this, "Description must be less than 300 characters", Toast.LENGTH_LONG).show()
                    return@setPositiveButton
                }
                // Update item
                val updatedItem = item.copy(
                    taskName = updatedTaskName,
                    taskDescriptions = updatedDescription,
                    dueDate = updatedDueDate
                )
                // Update in ViewModel
                viewModel.update(updatedItem)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }


}
