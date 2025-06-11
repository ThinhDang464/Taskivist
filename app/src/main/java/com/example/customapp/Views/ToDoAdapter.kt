package com.example.customapp.Views

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.customapp.R
import com.example.customapp.Model.ToDoItem

//handles displaying list of item in recycler view
class ToDoAdapter(private val listener: (ToDoItem) -> Unit) : ListAdapter<ToDoItem, ToDoAdapter.ViewHolder>(
    ToDoDiffCallback()
) {
    var onItemClicked: ((ToDoItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.todo, parent, false) as View
        return ViewHolder(view)
    }

    inner class ViewHolder(private val v: View) : RecyclerView.ViewHolder(v) {
        private val taskName: TextView = v.findViewById(R.id.taskNameTextView)
        private val description: TextView = v.findViewById(R.id.descriptionTextView)
        private val isChecked: CheckBox = v.findViewById(R.id.checkBox)
        private val dueDateTextView: TextView = v.findViewById(R.id.dueDateTextView)

        //handle on item click
        init {
            v.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) { // Check if position is valid
                    val item = getItem(position) //current recycler view item
                    onItemClicked?.invoke(item) //pass item as parameter to onItemClicked
                }
            }
        }


        fun bind(item: ToDoItem) {
            taskName.text = item.taskName
            description.text = item.taskDescriptions
            isChecked.isChecked = item.isChecked
            dueDateTextView.text = item.dueDate

            // take action when checkbox is checked:
            isChecked.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    item.isChecked = true
                    listener.invoke(item)  // Pass the ToDoItem directly to the listener.
                }
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}

class ToDoDiffCallback : DiffUtil.ItemCallback<ToDoItem>() {
    override fun areItemsTheSame(oldItem: ToDoItem, newItem: ToDoItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ToDoItem, newItem: ToDoItem): Boolean {
        return oldItem == newItem
    }
}
