package com.example.mahjongpointstracker.model

data class DataRow(
  val id: Int,
  val name: String,
  val points: Int
  // TODO add a column for hands
) {
  fun toPlayer() = Player(id, name, points)
}
