package dev.alcini.cprbeat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dev.alcini.cprbeat.ui.MainScreen
import dev.alcini.cprbeat.ui.theme.CprBeatTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            CprBeatTheme {
                MainScreen()
            }
        }
    }
}
