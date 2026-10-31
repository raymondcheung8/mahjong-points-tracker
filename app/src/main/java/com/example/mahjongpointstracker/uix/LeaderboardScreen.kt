package com.example.mahjongpointstracker.uix

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mahjongpointstracker.model.Player

@Composable
fun LeaderboardScreen() {
  //winnerId only has 1 slot for winner (change data type if more winners req.)
  var winnerId by remember { mutableStateOf<Int?>(null) }

  var players by remember {
    mutableStateOf(
      listOf(
        Player(1, "Alice", 120),
        Player(2, "Bob", 85),
        Player(3, "Chen", 240),
        Player(4, "Dana", 60)
      )
    )
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text("Leaderboard", style = MaterialTheme.typography.headlineMedium)
    Spacer(Modifier.height(16.dp))

    LazyColumn(modifier = Modifier.fillMaxWidth()) {
      itemsIndexed(
        items = players.sortedByDescending { it.points },
        key = { _, player -> player.id }
      ) { index, player ->
        Column(modifier = Modifier.animateItem()) {
          LeaderboardRow(
            rank = index + 1,
            player = player,
            isWinner = player.id == winnerId,

            //overwrite old check if new one is selected, clears winnerId if unselected
            onWinnerChange =  { checked ->
              winnerId = if (checked) player.id else null
            }
          )
          HorizontalDivider()
        }
      }
    }

    HorizontalDivider()
    Spacer(Modifier.height(12.dp))

    // Button panel
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      OutlinedButton(
        modifier = Modifier.weight(1f),
        enabled = false,
        onClick = { winnerId = null }
      ) {
        Text("Clear")
      }
      Button(
        modifier = Modifier.weight(1f),
        enabled = winnerId != null,
        onClick = {
          players = players.map {
            if (it.id == winnerId) it.copy(points = it.points + 30) else it
          }
          winnerId = null
        }
      ) {
        Text("Winner +30")
      }
    }
  }
}