package com.example.overrun.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.gohero.enitities.character.HeroCharacter
import com.example.gohero.enitities.character.HeroCompose
import com.example.overrun.enitities.Route.HOME
import com.example.overrun.enitities.Route.MAIN_MENU

@Composable
fun TestCharacterGameScreen(navController: NavController)
{
    val hero = HeroCharacter(Pair(0U, 0U))

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    )
    {
        Button(onClick = { navController.navigate(HOME.path) }) {
            Text("Back to Home")
        }

        BoxWithConstraints(modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            , contentAlignment = Alignment.Center)
        {
            hero.updatePosition((maxWidth / 2).value.toUInt(), (maxHeight / 2).value.toUInt())
            HeroCompose(hero)
        }
    }

}

@Preview(showBackground = true)
@Composable
fun Preview() {
    TestCharacterGameScreen(navController = rememberNavController())
}