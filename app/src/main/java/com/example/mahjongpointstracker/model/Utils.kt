package com.example.mahjongpointstracker.model

import android.util.Log
import java.io.File
import java.io.IOException

object Utils {
  private const val LOG_TAG = "Utils"

  fun <T> withRetries(limit: Int, action: () -> Result<T>): Result<T> {
    require(limit > 0) { "limit must be positive" }
    repeat(limit) {
      val result = action()
      if (result.isSuccess) return result
    }
    return Result.failure(IOException("Failed after $limit attempts"))
  }

  /** @return true if file is created, false if file already existed, an error otherwise
   */
  fun createNewFile(file: File): Result<Boolean> =
    runCatching { file.createNewFile() }
      .onSuccess { created -> if (created) Log.i(LOG_TAG, "Created new file: $file") }
      .onFailure { Log.w(LOG_TAG, "Failed to create file: $file", it) }

  fun readCsv(file: File): Result<List<List<String>>> = runCatching {
    file.bufferedReader().use { reader ->
      reader.lineSequence()
        .filter { it.isNotBlank() }
        .map { it.split(",") }
        .toList()
    }
  }

  fun writeCsv(file: File, rows: List<List<String>>): Result<Unit> = runCatching {
    file.bufferedWriter().use { writer ->
      rows.forEach { row ->
        writer.write(row.joinToString(","))
        writer.newLine()
      }
    }
  }

  //2^k until 4 faan, then alternates between 2*2^k and 3*2^k
  fun faanToPoints(faan: Int): Int {
    if (faan <= 4) return 1 shl faan

    val m = faan - 4
    val base = if (m % 2 == 0) 2 else 3
    return base * (1 shl (3 + m / 2))
  }
}
