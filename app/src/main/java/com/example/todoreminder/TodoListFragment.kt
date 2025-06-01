package com.example.todoreminder

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todoreminder.databinding.FragmentTodolistBinding
import com.example.todoreminder.models.Todo
import com.example.todoreminder.models.TodoAdapter
import com.example.todoreminder.retrofit.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

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
            super.onViewCreated(view, savedInstanceState)
            val sharedPref = requireActivity().getSharedPreferences("user_prefs", MODE_PRIVATE)
            val userId = sharedPref.getString("userId", null)


            recyclerView = binding.recyclerViewTodos
            recyclerView.layoutManager = LinearLayoutManager(requireContext())

            adapter = TodoAdapter(todoList) { todo ->
                // TODO: Aggiorna il completamento nel backend se necessario
                Toast.makeText(
                    requireContext(),
                    "Todo: ${todo.title} -> ${todo.completed}",
                    Toast.LENGTH_SHORT
                ).show()
                toggleTodoCompletion(todo)
            }

            recyclerView.adapter = adapter

            loadTodos(userId.toString())

            // Esempio fittizio di ToDo
//            todoList.addAll(listOf(
//                Todo(1, "Fare la spesa", false, System.currentTimeMillis(),"999"),
//                Todo(2, "mangiare", false, System.currentTimeMillis(),"999"),
//                Todo(3, "giocare", false, System.currentTimeMillis(),"999")
//            ))

//            adapter.notifyDataSetChanged()


    //        binding.buttonFirst.setOnClickListener {
    //            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
    //        }
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
                Toast.makeText(requireContext(), "Errore caricamento: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun toggleTodoCompletion(todo: Todo) {
//        todo.completed = !todo.completed // aggiorna localmente

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = RetrofitClient.api.completeTodo(todo.id!!) //sono sicuro non sia null!
                if (response.success) {
                    Toast.makeText(requireContext(), "Aggiornato: ${todo.title}", Toast.LENGTH_SHORT).show()
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(requireContext(), "Errore: ${response.message}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Errore update: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}