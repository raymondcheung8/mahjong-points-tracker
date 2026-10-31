package com.example.mahjongpointstracker.model

data class Player(
  val id: Int,
  val name: String,
  val points: Int = 0
)