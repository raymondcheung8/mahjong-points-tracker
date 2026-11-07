package com.example.mahjongpointstracker.leaderboard

import android.R
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mahjongpointstracker.model.Player
import com.example.mahjongpointstracker.model.Utils
import com.example.mahjongpointstracker.model.WinType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import java.io.File

class LeaderboardViewModel(application: Application) : AndroidViewModel(application) {
  private val dataFile = File(application.filesDir, "data.csv")
  private val _players = MutableStateFlow<List<Player>>(emptyList())
  val players: StateFlow<List<Player>> = _players.asStateFlow()

  init {
    viewModelScope.launch(Dispatchers.IO) {
      Utils.loadPlayerData(dataFile)
        .onSuccess { _players.value = it }
        .onFailure { Log.e("LeaderboardVM", "Failed to load CSV", it) }
    }
  }

  fun resetPoints() {
    val updated = _players.value.map { it.getPlayerWithPointsReset() }
    _players.value = updated
    save(updated)
  }

  private fun save(players: List<Player>) {
    viewModelScope.launch(Dispatchers.IO) {
      Utils.writeDataRows(dataFile, players.map { it.toDataRow() })
    }
  }

  fun renamePlayer(playerId: Int, newName: String) {
    val updated = _players.value.map {
      if (it.id == playerId) it.copy(name = newName.trim()) else it
    }
    _players.value = updated
    save(updated)
  }

  fun owed(p: Player, winType: WinType, dealerWon: Boolean, dealerId: Int, discarderId: Int?, unit: Int): Int {
    val base = when (winType) {
      WinType.SIKWU -> if (p.id == discarderId) 2 else 1
      WinType.ZIMO -> 2
    }
    val multiplier = if (dealerWon || p.id == dealerId) 2 else 1
    return base * unit * multiplier
  }

  fun recordWin(winnerId: Int, winType: WinType, discarderId: Int?, dealerId: Int, faan: Int) {
    val unit = Utils.faanToPoints(faan)
    val total = _players.value
      .filter { it.id != winnerId }
      .sumOf { owed(it, winType, winnerId == dealerId, dealerId, discarderId, unit) }

    val updated = _players.value.map {
      if (it.id == winnerId) it.copy(points = it.points + total)
      else it.copy(points = it.points - owed(it, winType, winnerId == dealerId, dealerId, discarderId, unit))
    }

    _players.value = updated
    save(updated)
  }
}
