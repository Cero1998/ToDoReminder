package com.example.todoreminder

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todoreminder.databinding.FragmentFirstBinding
import com.example.todoreminder.models.Todo

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TodoAdapter
    private val todoList = mutableListOf<Todo>()


    private var _binding: FragmentFirstBinding? = null
        // This property is only valid between onCreateView and
        // onDestroyView.
        private val binding get() = _binding!!

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {

          _binding = FragmentFirstBinding.inflate(inflater, container, false)
          return binding.root

        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)



            recyclerView = binding.recyclerViewTodos
            recyclerView.layoutManager = LinearLayoutManager(requireContext())

            adapter = TodoAdapter(todoList) { todo ->
                // TODO: Aggiorna il completamento nel backend se necessario
                Toast.makeText(requireContext(), "Todo: ${todo.title} -> ${todo.completed}", Toast.LENGTH_SHORT).show()
            }

            recyclerView.adapter = adapter

            // Esempio fittizio di ToDo
            todoList.addAll(listOf(
                Todo("1", "Fare la spesa", false),
                Todo("2", "Studiare Kotlin", true),
                Todo("3", "Portare fuori il cane", false),
                Todo("3", "Portare fuori il cane", false),
                Todo("3", "Portare fuori il cane", false),
                Todo("3", "Portare fuori il cane", false),
                Todo("3", "Portare fuori il cane", false),
                Todo("3", "Portare fuori il cane", false),
                Todo("3", "Portare fuori il cane", false),
                Todo("3", "Portare fuori il cane", false),
                Todo("3", "Portare fuori il cane", false),
                Todo("3", "Portare fuori il cane", false),
                Todo("3", "Portare fuori il cane", false),
                Todo("3", "Portare fuori il cane", false),
                Todo("3", "Portare fuori il cane", false),
                Todo("3", "Portare fuori il cane", false),
                Todo("3", "Portare fuori il cane", false),
                Todo("3", "Portare fuori il cane", false),
                Todo("3", "Portare fuori il cane", false),
                Todo("3", "Portare fuori il cane", false),
                Todo("3", "Portare fuori il cane", false),
                Todo("3", "Portare fuori il cane", false),
                Todo("3", "Portare fuori il cane", false),
                Todo("3", "Portare fuori il cane", false),
                Todo("3", "Portare fuori il cane", false),
                Todo("3", "Portare fuori il cane", false),
                Todo("3", "Portare fuori il cane", false),
                Todo("3", "Portare fuori il cane", false),
                Todo("3", "Portare fuori il cane", false),
            ))

            adapter.notifyDataSetChanged()


    //        binding.buttonFirst.setOnClickListener {
    //            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
    //        }
        }

    override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
}