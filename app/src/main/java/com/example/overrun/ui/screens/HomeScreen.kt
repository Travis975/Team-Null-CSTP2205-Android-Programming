package com.example.overrun.ui.screens

// import firebasse Oauth
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.overrun.R
import com.example.overrun.enitities.Route.*
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun HomeScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    // Animate vertical bounce
    val infiniteTransition = rememberInfiniteTransition()
    val bounceOffset by infiniteTransition.animateFloat(
        initialValue = 10f,
        targetValue = -10f, // move up
        animationSpec = infiniteRepeatable(
            animation = tween(300, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.forest1),
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
                painter = painterResource(id = R.drawable.overrun_logo),
                contentDescription = "Overrun Logo",
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        scaleY = 0.9f
                    }
                    .padding(horizontal = 16.dp),
                contentScale = ContentScale.FillWidth
            )

            Spacer(modifier = Modifier.height(24.dp))

            Image(
                painter = painterResource(id = R.drawable.slogan1),
                contentDescription = "slogan1",
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(64.dp))

            // Play game Button
            Button(
                onClick = {
                    if (currentUser != null) {
                        navController.navigate(MAIN_MENU.path) // Go to game
                    } else {
                        navController.navigate(SIGNUP.path) // Go to signup screen
                    }
                },
                modifier = Modifier.offset(y = bounceOffset.dp),
                colors = ButtonDefaults.buttonColors(
                    // containerColor = Color(0xFF1B5E20), // dark green background
                    contentColor = Color.White         // white text
                )
            ) {
                Text("Game Start",
                    fontSize = 20.sp,
                    modifier = Modifier
                        .padding(8.dp))
            }
            Spacer(modifier = Modifier.height(64.dp))

            // For making sign up UI
//            Button(
//                onClick = { navController.navigate(SIGNUP.path) },
//                modifier = Modifier.padding(12.dp)
//            ) {
//                Text("Sign up")
//            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally, // Centers images horizontally
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.slogan3_1),
                    contentDescription = "slogan3.1",
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentScale = ContentScale.Fit
                )

                Image(
                    painter = painterResource(id = R.drawable.slogan3_2),
                    contentDescription = "slogan3.2",
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            scaleY = 0.8f
                            scaleX = 0.8f
                        },
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}


// Preview 
@RequiresApi(Build.VERSION_CODES.Q)
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(navController = rememberNavController())
}
