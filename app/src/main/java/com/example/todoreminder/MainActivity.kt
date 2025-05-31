package com.example.todoreminder

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.todoreminder.retrofit.LoginRequest
import com.example.todoreminder.retrofit.LoginResponse
import com.example.todoreminder.retrofit.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button
    private val retrofitClient = RetrofitClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        emailEditText = findViewById(R.id.editTextMail)
        passwordEditText = findViewById(R.id.editTextPassword)
        loginButton = findViewById(R.id.buttonLogin)
        registerButton = findViewById(R.id.buttonRegister)


        registerButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill the Email and Password fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val request = LoginRequest(email, password)

            CoroutineScope(Dispatchers.Main).launch {
                try
                {
                    val response= RetrofitClient.api.register(request)
                    if (response.success)
                    {
                        Toast.makeText(this@MainActivity, response.message, Toast.LENGTH_SHORT).show()
                    }
                    else
                    {
                        Toast.makeText(this@MainActivity, response.error, Toast.LENGTH_SHORT).show()
                    }
                }
                catch (e: Exception)
                {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, "Exception: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill the Email and Password fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val request = LoginRequest(email, password)

            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val response = RetrofitClient.api.login(request)
                    if (response.success)
                    {
                            Toast.makeText(this@MainActivity, response.message, Toast.LENGTH_SHORT).show()
                            // TODO: Vai alla schermata ToDoList
                            startActivity(Intent(this@MainActivity, TodoActivity::class.java))
                    }
                    else
                    {
                        Toast.makeText(this@MainActivity, response.error, Toast.LENGTH_SHORT).show()
                    }
                }
                catch (e: Exception)
                {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, "Exception: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}