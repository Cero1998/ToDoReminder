package com.example.todoreminder

import android.content.Context.MODE_PRIVATE
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todoreminder.databinding.FragmentTodolistBinding
import com.example.todoreminder.models.Todo
import com.example.todoreminder.models.TodoAdapter
import com.example.todoreminder.retrofit.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class TodoListFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TodoAdapter
    private val todoList = mutableListOf<Todo>()


        private var _binding: FragmentTodolistBinding? = null
        // This property is only valid between onCreateView and
        // onDestroyView.
        private val binding get() = _binding!!

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {

          _binding = FragmentTodolistBinding.inflate(inflater, container, false)
          return binding.root

        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        showExitConfirmationDialog()
                    }
                })

            super.onViewCreated(view, savedInstanceState)
            val sharedPref = requireActivity().getSharedPreferences("user_prefs", MODE_PRIVATE)
            val userId = sharedPref.getString("userId", null)

            recyclerView = binding.recyclerViewTodos
            recyclerView.layoutManager = LinearLayoutManager(requireContext())

            adapter = TodoAdapter(todoList) { todo ->
                toggleTodoCompletion(todo)
            }
            recyclerView.adapter = adapter


            loadTodos(userId.toString())


            val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ) = false

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    val todo = todoList[position]
                    deleteTodo(todo.id!!)
                }

                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {
                    val itemView = viewHolder.itemView

                    if (dX > 0) {
                        val background = ColorDrawable(Color.RED)
                        background.setBounds(
                            itemView.left,
                            itemView.top,
                            itemView.left + dX.toInt(),
                            itemView.bottom
                        )
                        background.draw(c)

                        val paint = Paint().apply {
                            color = Color.WHITE
                            textSize = 40f
                            isAntiAlias = true
                        }

                        val text = "Delete"
                        val textHeight = paint.descent() - paint.ascent()
                        val textOffset = textHeight / 2 - paint.descent()
                        val x = itemView.left + 50f
                        val y = itemView.top + itemView.height / 2f + textOffset

                        c.drawText(text, x, y, paint)
                    } else {
                        val clear = Paint().apply {
                            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
                        }
                        c.drawRect(
                            itemView.left.toFloat(),
                            itemView.top.toFloat(),
                            itemView.right.toFloat(),
                            itemView.bottom.toFloat(),
                            clear
                        )
                    }
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                }
            }
            ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView)
        }

    override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
    }

    private fun loadTodos(userId: String) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val todos = RetrofitClient.api.getTodos(userId)
                todoList.clear()
                todoList.addAll(todos)
                adapter.notifyDataSetChanged()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Exception: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun toggleTodoCompletion(todo: Todo) {
        val completedOrNot = todo.completed

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = RetrofitClient.api.completeTodo(todo.id!!, completedOrNot) //sono sicuro non sia null!
                if (response.success) {
                    Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Exception: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun deleteTodo(id: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.api.deleteTodo(id)
                CoroutineScope(Dispatchers.Main).launch {
                    if (response.success)
                    {
                        val position = todoList.indexOfFirst { it.id == id }
                        if (position != -1) {
                            todoList.removeAt(position)
                            adapter.notifyItemRemoved(position)
                        }
                        Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
                    }
                    else
                    {
                        Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Exception: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Exit app")
            .setMessage("Are you sure you want to exit the app?")
            .setPositiveButton("Yes") { _, _ ->
                requireActivity().finish()
            }
            .setNegativeButton("No", null)
            .show()
    }

}