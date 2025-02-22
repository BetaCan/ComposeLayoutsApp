package com.example.composelayoutsapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.composelayoutsapp.ui.theme.ComposeLayoutsAppTheme
import java.util.regex.Pattern

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeLayoutsAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation() // Centralized navigation for all screens
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login" // Start at the Login Screen
    ) {
        composable("login") { LoginScreen(navController) }
        composable("signup") { SignUpScreen(navController) }
        composable("forgot_password") { ForgotPasswordScreen(navController) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavHostController) {
    // State for Email and Email Validation
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }

    // State for Password and Password Validation
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }

    // Helper function to validate email using a regex pattern
    fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"
        return Pattern.compile(emailPattern).matcher(email).matches()
    }

    // Helper function to validate password strength
    fun isStrongPassword(password: String): Boolean {
        val passwordPattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}\$"
        return Pattern.compile(passwordPattern).matcher(password).matches()
    }

    // Validate function to check both email and password
    fun validateFields() {
        emailError = !isValidEmail(email) // Check if email is valid
        passwordError = !isStrongPassword(password) // Check if password meets strength criteria
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Login Screen") },
                colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.primary)
            )
        },
        content = { innerPadding ->
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                val (logo, emailField, passwordField, buttonRow, forgotPassword) = createRefs()

                // 1. Logo/Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp)
                        .constrainAs(logo) {
                            top.linkTo(parent.top)
                            centerHorizontallyTo(parent)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text("App Logo", style = MaterialTheme.typography.headlineMedium)
                }

                // 2. Email TextField with validation
                Column(modifier = Modifier.constrainAs(emailField) {
                    top.linkTo(logo.bottom, margin = 32.dp)
                }) {
                    TextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        isError = emailError,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                    if (emailError) {
                        Text(
                            text = "Invalid email address",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }

                // 3. Password TextField with validation and animation
                Column(modifier = Modifier.constrainAs(passwordField) {
                    top.linkTo(emailField.bottom)
                }) {
                    val alpha by animateFloatAsState(
                        targetValue = if (passwordVisible) 1f else 0.5f
                    )

                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(
                                onClick = { passwordVisible = !passwordVisible },
                                modifier = Modifier.alpha(alpha)
                            ) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password"
                                )
                            }
                        },
                        isError = passwordError,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    if (passwordError) {
                        Text(
                            text = "Password must be at least 8 characters long, contain an uppercase letter, a number, and a special character.",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }

                // 4. Row for Login and Sign Up Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .constrainAs(buttonRow) {
                            top.linkTo(passwordField.bottom, margin = 24.dp)
                        },
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = { validateFields() }) {
                        Text("Login")
                    }
                    Button(onClick = { navController.navigate("signup") }) {
                        Text("Sign Up")
                    }
                }

                // 5. Forgot Password Text Button
                TextButton(
                    onClick = { navController.navigate("forgot_password") },
                    modifier = Modifier.constrainAs(forgotPassword) {
                        top.linkTo(buttonRow.bottom, margin = 16.dp)
                        centerHorizontallyTo(parent)
                    }
                ) {
                    Text("Forgot Password?")
                }
            }
        }
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SignUpScreen(navController: NavHostController) {
    Scaffold {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Button(onClick = { navController.popBackStack() }) {
                Text("Back to Login")
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ForgotPasswordScreen(navController: NavHostController) {
    Scaffold {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Button(onClick = { navController.popBackStack() }) {
                Text("Back to Login")
            }
        }
    }
}
