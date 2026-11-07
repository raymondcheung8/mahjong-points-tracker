package com.example.mahjongpointstracker.model

data class DataRow(
  val id: Int,
  val name: String,
  val points: Int
  // TODO add a column for hands
) {
  fun toPlayer() = Player(id, name, points)
  fun toStringList() = listOf(id.toString(), name, points.toString())

  companion object {
    fun getHeader() = listOf(DataRow::id.name, DataRow::name.name, DataRow::points.name)
  }
}
