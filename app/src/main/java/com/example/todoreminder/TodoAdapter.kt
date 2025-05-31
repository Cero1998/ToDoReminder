package com.example.todoreminder


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todoreminder.R
import com.example.todoreminder.models.Todo

class TodoAdapter(
    private val todos: MutableList<Todo>,
    private val onCheckChanged: (Todo) -> Unit
) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBoxCompleted)
        val title: TextView = itemView.findViewById(R.id.textViewTodoTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.todo_item, parent, false)
        return TodoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val todo = todos[position]
        holder.title.text = todo.title
        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.isChecked = todo.completed



        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            todo.completed = isChecked
            onCheckChanged(todo)
        }
    }

    override fun getItemCount(): Int = todos.size
}
