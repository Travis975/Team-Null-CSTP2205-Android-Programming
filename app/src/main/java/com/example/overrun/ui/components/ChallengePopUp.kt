package com.example.overrun.ui.components

import android.os.Handler
import android.os.Looper
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.overrun.repository.FirestoreRepository

// Function to show the popup and save to Firestore
@Composable
fun ChallengePopUp(message: String, userId: String, levelChallenges: Map<String, Boolean>, characterChallenges: Map<String, Boolean>, onDismiss: () -> Unit) {
    val snackbarHostState = remember { androidx.compose.material3.SnackbarHostState() }

    // Show the Snackbar with the challenge completion message
    LaunchedEffect(message) {
        snackbarHostState.showSnackbar(
            message = message,
            duration = SnackbarDuration.Short
        )
    }

    // Display the Snackbar
    androidx.compose.material3.SnackbarHost(hostState = snackbarHostState) {
        Snackbar(
            modifier = Modifier,
            containerColor = Color.Green, // Snackbar background color
            contentColor = Color.White, // Text color
        ) {
            Text(
                text = message,
                fontSize = 16.sp
            )
        }
    }

    // Dismiss the snackbar after 3 seconds and save challenge completion to Firestore
    Handler(Looper.getMainLooper()).postDelayed({
        onDismiss() // Hide the message after it disappears
        saveChallengeToFirestore(userId, levelChallenges, characterChallenges) // Save to Firestore
    }, 3000) // Popup dismisses after 3 seconds
}

// Function to save the challenge completion to Firestore (using your existing repository)
fun saveChallengeToFirestore(userId: String, levelChallenges: Map<String, Boolean>, characterChallenges: Map<String, Boolean>) {
    val firestoreRepository = FirestoreRepository()

    // Save the challenge completion to Firestore
    firestoreRepository.saveChallengeCompletion(userId, levelChallenges, characterChallenges)
}
