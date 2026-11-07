package com.example.mahjongpointstracker.model

data class Player(
  val id: Int,
  val name: String,
  val points: Int
) {
  fun toDataRow() = DataRow(id, name, points)
  fun getPlayerWithPointsReset() = Player(id, name, DEFAULT_POINTS)

  companion object {
    const val DEFAULT_POINTS = 100
    fun default(id: Int) = Player(id, "Player$id", DEFAULT_POINTS)
    fun getDefaultPlayers(): List<Player> =
      listOf(
        default(1),
        default(2),
        default(3),
        default(4),
      )
  }
}
