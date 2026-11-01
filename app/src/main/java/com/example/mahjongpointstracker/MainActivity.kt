package com.example.mahjongpointstracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mahjongpointstracker.uix.LeaderboardScreen

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      )
      {
        LeaderboardScreen()
      }
    }
  }
}
