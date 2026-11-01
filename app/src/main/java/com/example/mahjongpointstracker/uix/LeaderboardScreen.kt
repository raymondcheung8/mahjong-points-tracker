package com.example.mahjongpointstracker.uix

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.mahjongpointstracker.model.Player
import com.example.mahjongpointstracker.model.Utils
import com.example.mahjongpointstracker.model.WinType
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

  var showWinnerDialog by remember { mutableStateOf(false) }
  var winType by remember { mutableStateOf<WinType?>(null) }
  var discarderId by remember { mutableStateOf<Int?>(null) }

  var players by remember {
    mutableStateOf(
      listOf(
        Player(1, "Player1", 300),
        Player(2, "Player2", 300),
        Player(3, "Player3", 300),
        Player(4, "Player4", 300)
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
    //TODO add zimo and sikwu checkboxes maybe in winner popup
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      OutlinedButton(
        modifier = Modifier.weight(1f),
        onClick = {
          winnerId = null
          //set all player points to 300
          players = players.map { it.copy(points = 300) }
        }
      ) {
        Text("Reset")
      }
      Button(
        modifier = Modifier.weight(1f),
        enabled = winnerId != null,
        onClick = {
          // TODO update csv file
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

  //popup to input faan into leaderboard
  if (showWinnerDialog) {
    val winner = players.find { it.id == winnerId }
    var pointsText by remember { mutableStateOf("") }
    val points = pointsText.toIntOrNull()

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
          OutlinedTextField(
            value = pointsText,
            onValueChange = { input -> pointsText = input.filter { it.isDigit() } },
            singleLine = true,
            label = { Text("Faan (番)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
          )
        }
      },
      confirmButton = {
        TextButton(
          enabled = points != null && winType != null && (winType != WinType.SIKWU || discarderId != null),
          onClick = {
            players = players.map {
              //minus points from non-winners, extra penalty if dealt in
              //all penalty if self draw
              //convert faan to points before adding to final score
              when (winType) {
                WinType.SIKWU -> {
                  when (it.id) {
                    discarderId -> it.copy(points = it.points - 2 * Utils.faanToPoints(points!!))
                    winnerId -> it.copy(points = it.points + 4 * Utils.faanToPoints(points!!))
                    else -> it.copy(points = it.points - Utils.faanToPoints(points!!))
                  }
                }
                WinType.ZIMO -> {
                  when (it.id) {
                    winnerId -> it.copy(points = it.points + 6 * Utils.faanToPoints(points!!))
                    else -> it.copy(points = it.points - 2 * Utils.faanToPoints(points!!))
                  }
                }
                else -> it
              }
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
