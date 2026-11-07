package com.example.mahjongpointstracker.leaderboard

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

  fun recordWin(winnerId: Int, winType: WinType, discarderId: Int?, faan: Int) {
    val unit = Utils.faanToPoints(faan)

    val updated = _players.value.map {
      when (winType) {
        // minus points from non-winners, extra penalty if dealt in
        // all penalty if self draw
        // convert faan to points before adding to final score
        WinType.SIKWU -> when (it.id) {
          discarderId -> it.copy(points = it.points - 2 * unit)
          winnerId -> it.copy(points = it.points + 4 * unit)
          else -> it.copy(points = it.points - unit)
        }

        WinType.ZIMO -> when (it.id) {
          winnerId -> it.copy(points = it.points + 6 * unit)
          else -> it.copy(points = it.points - 2 * unit)
        }
      }
    }

    _players.value = updated
    save(updated)
  }
}
