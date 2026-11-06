package com.example.mahjongpointstracker.model

data class Player(
  val id: Int,
  val name: String,
  val points: Int = 0
) {
  companion object {
    fun default(id: Int) = Player(id, "Player$id", 100)
    fun getDefaultPlayers(): List<Player> =
      listOf(
        default(1),
        default(2),
        default(3),
        default(4),
      )
  }
}
