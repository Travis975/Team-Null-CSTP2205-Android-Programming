import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.overrun.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.overrun.enitities.Route.*

@Composable
fun SignUpScreen(navController: NavController) {
    val context = LocalContext.current
    val auth = remember { FirebaseAuth.getInstance() }
    val db = remember { FirebaseFirestore.getInstance() }

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }


    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.grass_dungeon),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Foreground Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Image(
                painter = painterResource(id = R.drawable.sign_up_text),
                contentDescription = "Logo",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentScale = ContentScale.FillWidth
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)  // 80% width of the screen
                    .background(
                        color = Color.White.copy(alpha = 0.8f),
                        shape = RoundedCornerShape(12.dp)  // Rounded corners
                    )
                    .padding(16.dp)
            ) {
                Column {
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username") }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // "By signing up you agree to our privacy policy and terms and conditions"
                    val privacyText = buildAnnotatedString {
                        append("By signing up you agree to our ")
                        withStyle(style = SpanStyle(color = Color.Blue),) {
                            append("privacy policy")
                        }
                    }

                    ClickableText(
                        text = privacyText,
                        onClick = {
                            // Navigate to SignIn screen when clicked
                            navController.navigate(PRIVACY.path)
                        }
                    )
                    val termsText = buildAnnotatedString {
                        append("and ")
                        withStyle(style = SpanStyle(color = Color.Blue),) {
                            append("terms and conditions")
                        }
                    }

                    ClickableText(
                        text = termsText,
                        onClick = {
                            // Navigate to SignIn screen when clicked
                            navController.navigate(TERMS.path)
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    // "Already have an account? Sign in here"
                    val accountText = buildAnnotatedString {
                        append("Already have an account? ")
                        withStyle(style = SpanStyle(color = Color.Blue)) {
                            append("Sign in here")
                        }
                    }

                    ClickableText(
                        text = accountText,
                        onClick = {
                            // Navigate to SignIn screen when clicked
                            navController.navigate(SIGNIN.path)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    // Validation to prevent empty sign-ups
                    if (username.isBlank() || email.isBlank() || password.isBlank()) {
                        Toast.makeText(context, "All fields are required!", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    isLoading = true
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val userId = auth.currentUser?.uid
                                if (userId != null) {
                                    val userData = hashMapOf(
                                        "username" to username,
                                        "email" to email
                                    )

                                    // Store username in Firestore
                                    db.collection("users").document(userId).set(userData)
                                        .addOnSuccessListener {
                                            Toast.makeText(
                                                context,
                                                "Sign-up Successful!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            navController.navigate(MAIN_MENU.path)
                                        }
                                        .addOnFailureListener { e ->
                                            Toast.makeText(
                                                context,
                                                "Error saving username: ${e.message}",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    "Sign-up Failed: ${task.exception?.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            isLoading = false
                        }
                },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    // containerColor = Color(0xFFFF9800), // orange background
                    contentColor = Color.White          // white text
                )
            ) {
                Text("Sign Up")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { navController.navigate(HOME.path) },
                colors = ButtonDefaults.buttonColors(
                    // containerColor = Color(0xFFFF9800), // orange background
                    contentColor = Color.White          // white text
                )
            ) {
                Text("Back")
            }

        }
    }
}
