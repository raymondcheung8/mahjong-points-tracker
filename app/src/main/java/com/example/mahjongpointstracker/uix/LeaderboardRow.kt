package com.example.mahjongpointstracker.uix

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mahjongpointstracker.model.Player

@Composable
fun LeaderboardRow(
    rank: Int,
    player: Player,
    onAddPoints: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$rank",
            modifier = Modifier.width(32.dp),
            fontWeight = FontWeight.Bold
        )
        Text(
            text = player.name,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "${player.points}",
            modifier = Modifier.padding(end = 8.dp),
            fontWeight = FontWeight.Bold
        )
        OutlinedButton(onClick = { onAddPoints(-10) }) { Text("-10") }
        Spacer(Modifier.width(4.dp))
        Button(onClick = { onAddPoints(10) }) { Text("+10") }
    }
}