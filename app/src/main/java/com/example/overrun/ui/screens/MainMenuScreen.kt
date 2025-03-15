package com.example.overrun.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.overrun.R
import com.example.overrun.enitities.Route.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun MainMenuScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val db = FirebaseFirestore.getInstance()

    var username by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(user) {
        // Redirect to signup if not logged in
        if (user == null) {
            navController.navigate(SIGNUP.path)
        } else {
            db.collection("users").document(user.uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        username = document.getString("username")
                    }
                    isLoading = false
                }
                .addOnFailureListener {
                    isLoading = false
                }
        }
    }

    LaunchedEffect(username, isLoading) {
        if (!isLoading && username == null) {
            navController.navigate(SIGNUP.path)
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Loading...")
        }
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.forest_frawing),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Username Display in Top-Left Corner
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.TopStart // Position at top-left
        ) {
            Text(text = "Hi, ${username ?: "Player"}!",
                modifier = Modifier.padding(top =  32.dp),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
                )
        }

        // Foreground Content
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.main_menu_text),
                contentDescription = "Main Menu Text",
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                contentScale = ContentScale.FillWidth
            )

            Spacer(modifier = Modifier.height(96.dp))

            Button(
                onClick = { navController.navigate(START_GAME.path) },
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                Text("Start Game")
            }

            Spacer(modifier = Modifier.height(36.dp))

            Button(
                onClick = { navController.navigate(CONTROLS.path) },
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                Text("Controls")
            }

            Spacer(modifier = Modifier.height(36.dp))

            Button(
                onClick = { navController.navigate(HOME.path) },
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                Text("Quit")
            }
        }
    }
}
