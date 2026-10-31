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
                        onAddPoints = { amount ->
                            players = players.map {
                                if (it.id == player.id) it.copy(points = it.points + amount) else it
                            }
                        }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}