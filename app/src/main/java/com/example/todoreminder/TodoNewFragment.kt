package com.example.todoreminder

import android.Manifest
import android.app.DatePickerDialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.CalendarContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.todoreminder.databinding.FragmentTodonewBinding
import com.example.todoreminder.retrofit.CreateTodoRequest
import com.example.todoreminder.retrofit.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class TodoNewFragment : Fragment() {

    private lateinit var calendarPermissionsLauncher: ActivityResultLauncher<Array<String>>
    private var pendingCalendarData: Pair<String, Long>? = null

    private var _binding: FragmentTodonewBinding? = null
    private var selectedDate: Long = System.currentTimeMillis()
    private var shouldNavigateAfterPermissions = false


    // This property is only valid between onCreateView and
        // onDestroyView.
        private val binding get() = _binding!!

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {



            calendarPermissionsLauncher = registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                val granted = permissions[Manifest.permission.WRITE_CALENDAR] == true &&
                        permissions[Manifest.permission.READ_CALENDAR] == true
                if (granted) {
                    pendingCalendarData?.let { (title, date) ->
                        val oneDayInMillis = 24 * 60 * 60 * 1000
                        val oneMinuteInMillis = 60 * 1000
                        val intent = Intent(Intent.ACTION_INSERT)
                            .setData(CalendarContract.Events.CONTENT_URI)
                            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, date)
                            .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, date + oneDayInMillis - oneMinuteInMillis)
                            .putExtra(CalendarContract.Events.TITLE, title)
                            .putExtra(CalendarContract.Events.DESCRIPTION, "Created with ToDoReminder app")
                            .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
                        startActivity(intent)
                    }
                } else {
                    Toast.makeText(requireContext(), "Calendar permissions denied", Toast.LENGTH_SHORT).show()
                }

                if (shouldNavigateAfterPermissions) {
                    shouldNavigateAfterPermissions = false
                    findNavController().navigate(R.id.action_TodoNewFragment_to_TodoListFragment)
                }
            }


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
                        if (response.success)
                        {
                            Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()

                            if(hasNotificationPermissions())
                            {
                                scheduleNotificationWithWorkManager(requireContext(), title, selectedDate)
                            }

                            if(binding.switch1SaveOnCalendar.isChecked)
                            {
                                pendingCalendarData = Pair(title, selectedDate)
                                if (!hasCalendarPermissions()) {
                                    shouldNavigateAfterPermissions = true
                                    calendarPermissionsLauncher.launch(
                                        arrayOf(
                                            Manifest.permission.WRITE_CALENDAR,
                                            Manifest.permission.READ_CALENDAR
                                        )
                                    )
                                    return@launch // Interrompe il proseguimento. Parte il codice di calendarPermissionsLauncher. Come break in un ciclo.
                                }
                                else
                                {
                                    val oneDayInMillis = 24 * 60 * 60 * 1000
                                    val oneMinuteInMillis = 60 * 1000
                                    val intent = Intent(Intent.ACTION_INSERT)
                                        .setData(CalendarContract.Events.CONTENT_URI)
                                        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, selectedDate)
                                        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, selectedDate + oneDayInMillis - oneMinuteInMillis)
                                        .putExtra(CalendarContract.Events.TITLE, title)
                                        .putExtra(CalendarContract.Events.DESCRIPTION, "Created with ToDoReminder app")
                                        .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
                                    startActivity(intent)
                                }
                            }

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

    private fun hasCalendarPermissions(): Boolean {
        val context = requireContext()
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED
    }

    private fun hasNotificationPermissions(): Boolean{
        val context = requireContext()
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    }

    fun scheduleNotificationWithWorkManager(context: Context, todoTitle: String, todoDateMillis: Long) {
        val now = System.currentTimeMillis()
        val triggerTime = Calendar.getInstance().apply {
            timeInMillis = todoDateMillis
            add(Calendar.DAY_OF_YEAR, -1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val delay = triggerTime - now

        val data = workDataOf("title" to todoTitle)

        val builder = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInputData(data)

        if (delay > 0) {
            builder.setInitialDelay(delay, TimeUnit.MILLISECONDS)
        }

        val workRequest = builder.build()
        WorkManager.getInstance(context).enqueue(workRequest)
    }
}