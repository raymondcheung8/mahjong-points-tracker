package com.example.mahjongpointstracker.uix

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.foundation.clickable
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
  isWinner: Boolean,
  onWinnerChange: (Boolean) -> Unit,
  onNameClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Checkbox(
      checked = isWinner,
      onCheckedChange = onWinnerChange
    )
    Text(
      text = "$rank",
      modifier = Modifier.width(32.dp),
      fontWeight = FontWeight.Bold
    )
    Text(
      text = player.name,
      modifier = Modifier
        .weight(1f)
        .clickable { onNameClick() },
      style = MaterialTheme.typography.bodyLarge
    )
    Text(
      text = "${player.points}",
      modifier = Modifier.padding(end = 8.dp),
      fontWeight = FontWeight.Bold
    )
  }
}
