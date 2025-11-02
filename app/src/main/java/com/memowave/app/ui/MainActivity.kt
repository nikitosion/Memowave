package com.memowave.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.memowave.app.ui.navigation.NavGraph
import com.memowave.app.ui.theme.MemowaveTheme
import com.memowave.app.ui.theme.backgroundLight

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Memowave()
        }
    }
}

@Composable
fun Memowave() {
    val navController = rememberNavController()

    MemowaveTheme {
        Surface(
            modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
            color = backgroundLight
        ) {
            NavGraph(navController)
        }
    }
}