package com.example.mahjongpointstracker.uix

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.mahjongpointstracker.model.Player
import com.example.mahjongpointstracker.model.Utils
import java.io.File

@Composable
fun LeaderboardScreen() {
  // TODO move to form screen instead of leaderboard screen
  val historyFile = File(LocalContext.current.filesDir, "example.csv")

  val retryLimit = 5
  if (!Utils.createNewFileWithRetries(historyFile, retryLimit)) {
    throw Exception("Cannot create new file: ${historyFile.name} after $retryLimit attempts")
  }
  // TODO read the existing scores from csv
  // TODO create a new function in Utils to read csv
//  val bufferedReader: BufferedReader = history.bufferedReader()
//  val inputString = bufferedReader.use { it.readText() }
//  println(inputString)

  //winnerId only has 1 slot for winner (change data type if more winners req.)
  var winnerId by remember { mutableStateOf<Int?>(null) }

  //edit player name
  var editingPlayer by remember { mutableStateOf<Player?>(null) }

  var players by remember {
    mutableStateOf(
      listOf(
        Player(1, "Player1", 0),
        Player(2, "Player2", 0),
        Player(3, "Player3", 0),
        Player(4, "Player4", 0)
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

    // Button panel
    //TODO reset score button
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      OutlinedButton(
        modifier = Modifier.weight(1f),
        onClick = {
          winnerId = null
          //set all player scores to 0
          players = players.map { it.copy(points = 0) }
        }
      ) {
        Text("Reset")
      }
      Button(
        modifier = Modifier.weight(1f),
        enabled = winnerId != null,
        onClick = {
          // TODO update csv file
          // TODO let user set faan
          players = players.map {
            if (it.id == winnerId) it.copy(points = it.points + 30) else it
          }
          winnerId = null

          Utils.faanToPoints(10)
        }
      ) {
        Text("Winner +30")
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
            players = players.map {
              if (it.id == player.id) it.copy(name = newName.trim()) else it
            }
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
}
