package com.example.mahjongpointstracker.leaderboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mahjongpointstracker.model.Player
import com.example.mahjongpointstracker.model.WinType
import kotlin.math.roundToInt

@Composable
fun LeaderboardScreen(viewModel: LeaderboardViewModel = viewModel()) {
  val players by viewModel.players.collectAsState()

  //winnerId only has 1 slot for winner (change data type if more winners req.)
  var winnerId by remember { mutableStateOf<Int?>(null) }

  var dealerId by remember { mutableStateOf<Int?>(null) }

  //edit player name
  var editingPlayer by remember { mutableStateOf<Player?>(null) }

  var faan by remember { mutableIntStateOf(3) }
  var showWinnerDialog by remember { mutableStateOf(false) }
  var winType by remember { mutableStateOf<WinType?>(null) }
  var discarderId by remember { mutableStateOf<Int?>(null) }

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
          dealerId = 1
          LeaderboardRow(
            rank = index + 1,
            player = player,
            isWinner = player.id == winnerId,
            isDealer = player.id == dealerId,

            //overwrite old check if new one is selected, clears winnerId if unselected
            onWinnerChange = { checked ->
              winnerId = if (checked) player.id else null
            },
            onNameClick = { editingPlayer = player }
          )
          HorizontalDivider()
        }
      }
    }

    HorizontalDivider()
    Spacer(Modifier.height(12.dp))

    Spacer(Modifier.height(12.dp))

    // Button panel
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      OutlinedButton(
        modifier = Modifier.weight(1f),
        onClick = {
          winnerId = null
          viewModel.resetPoints()
        }
      ) {
        Text("Reset")
      }
      Button(
        modifier = Modifier.weight(1f),
        enabled = winnerId != null,
        onClick = {
          showWinnerDialog = true
        }
      ) {
        Text("Winner")
      }
    }
  }

  //popup to change player name
  editingPlayer?.let { player ->
    var newName by remember(player.id) { mutableStateOf(player.name) }

    AlertDialog(
      onDismissRequest = { editingPlayer = null },
      title = { Text("Change name") },
      text = {
        OutlinedTextField(
          value = newName,
          onValueChange = { newName = it },
          singleLine = true,
          label = { Text("Name") }
        )
      },
      confirmButton = {
        TextButton(
          enabled = newName.isNotBlank(),
          onClick = {
            viewModel.renamePlayer(player.id, newName)
            editingPlayer = null
          }
        ) {
          Text("Save")
        }
      },
      dismissButton = {
        TextButton(onClick = { editingPlayer = null }) {
          Text("Cancel")
        }
      }
    )
  }

  //popup to input faan into leaderboard
  if (showWinnerDialog) {
    val winner = players.find { it.id == winnerId }

    AlertDialog(
      onDismissRequest = { showWinnerDialog = false },
      title = { Text("Faan (番) for ${winner?.name ?: "winner"}") },
      text = {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
              checked = winType == WinType.ZIMO,
              onCheckedChange = { checked ->
                winType = if (checked) WinType.ZIMO else null
                discarderId = null
              }
            )
            Text("Self draw (自莫)")
            Spacer(Modifier.width(16.dp))
            Checkbox(
              checked = winType == WinType.SIKWU,
              onCheckedChange = { checked ->
                winType = if (checked) WinType.SIKWU else null
                discarderId = null
              }
            )
            Text("Discard win (食糊)")
          }

          if (winType == WinType.SIKWU) {
            Spacer(Modifier.height(8.dp))
            Text("Who dealt in?", style = MaterialTheme.typography.labelLarge)
            players.filter { it.id != winnerId }.forEach { p ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clickable { discarderId = p.id },
                verticalAlignment = Alignment.CenterVertically
              ) {
                RadioButton(
                  selected = discarderId == p.id,
                  onClick = { discarderId = p.id }
                )
                Text(p.name)
              }
            }
          }

          Spacer(Modifier.height(8.dp))
          Text("Faan (番): $faan", style = MaterialTheme.typography.labelLarge)
          Slider(
            value = faan.toFloat(),
            onValueChange = { faan = it.roundToInt() },
            valueRange = 0f..10f,
            steps = 9
          )

          // TODO: Add this back once we connect this up to the CSV reader/writer
//          Spacer(Modifier.height(8.dp))
//
//          //Declare constants/values
//          var handText by remember { mutableStateOf("") }
//
//          //Textbox for writing out hands. Just copying Faan code to see how it works
//          OutlinedTextField(
//            value = handText,
//            onValueChange = { input -> handText = input.filter { it.isLetterOrDigit() } },
//            singleLine = true,
//            label = { Text("Hand") }
//          )
        }
      },
      confirmButton = {
        TextButton(
          enabled = winType != null && (winType != WinType.SIKWU || discarderId != null),
          onClick = {
            val winner = winnerId
            val type = winType

            if (winner != null && type != null) {
              viewModel.recordWin(winner, type, discarderId, dealerId, faan)
            }

            winnerId = null
            showWinnerDialog = false
          }
        ) {
          Text("Confirm")
        }
      },
      dismissButton = {
        TextButton(onClick = { showWinnerDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }
}
