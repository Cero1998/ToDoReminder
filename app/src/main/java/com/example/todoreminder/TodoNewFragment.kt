package com.example.todoreminder

import android.app.DatePickerDialog
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.todoreminder.MainActivity
import com.example.todoreminder.databinding.FragmentTodonewBinding
import com.example.todoreminder.retrofit.CreateTodoRequest
import com.example.todoreminder.retrofit.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class TodoNewFragment : Fragment() {

    private var _binding: FragmentTodonewBinding? = null
    private var selectedDate: Long = System.currentTimeMillis()

        // This property is only valid between onCreateView and
        // onDestroyView.
        private val binding get() = _binding!!

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {

          _binding = FragmentTodonewBinding.inflate(inflater, container, false)
          return binding.root

        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            binding.buttonSelectDate.setOnClickListener {
                showDatePickerDialog()
            }
            binding.textViewSelectedDate.text = "Date: ${android.text.format.DateFormat.format("dd/MM/yyyy", selectedDate)}"

            binding.buttonSaveNewTodo.setOnClickListener {
                //logica per salvare

                val title = binding.editTextNewTodoName.text.toString().trim()

                if (title.isEmpty()) {
                    Toast.makeText(requireContext(), "Please insert a valid name", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val sharedPref = requireActivity().getSharedPreferences("user_prefs", MODE_PRIVATE)
                val userId = sharedPref.getString("userId", null)


                val todoRequest = CreateTodoRequest(
                    title = title,
                    completed = false,
                    date = selectedDate,
                    userId = userId ?: ""
                )


                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        val response = RetrofitClient.api.createTodo(todoRequest)
                        if (response.success) {
                            Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.action_TodoNewFragment_to_TodoListFragment)
                        } else {
                            Toast.makeText(requireContext(), response.error, Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Exception: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                val selectedCalendar = Calendar.getInstance().apply {
                    set(year, month, day)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                selectedDate = selectedCalendar.timeInMillis
                binding.textViewSelectedDate.text = "Date: ${android.text.format.DateFormat.format("dd/MM/yyyy", selectedDate)}"
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = System.currentTimeMillis()
        }.show()
    }
}