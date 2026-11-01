package com.example.mahjongpointstracker.model

import android.util.Log
import java.io.File

object Utils {
  private const val LOG_TAG = "Utils"

  fun createNewFile(file: File): Boolean {
    val isCreated = file.createNewFile()
    if (isCreated) Log.i(LOG_TAG, "Created new file: $file")
    return isCreated
  }

  fun checkFileExistsElseCreate(file: File): Boolean {
    return file.exists() || createNewFile(file)
  }

  fun createNewFileWithRetries(file: File, limit: Int, retryCount: Int = 0): Boolean {
    if (retryCount == limit) Log.w(LOG_TAG, "Could not create new file after $limit tries")
    return retryCount != limit &&
        (checkFileExistsElseCreate(file) || createNewFileWithRetries(file, limit, retryCount + 1))
  }

  //2^k until 4 faan, then alternates between 2*2^k and 3*2^k
  fun faanToPoints(faan: Int): Int {
    if (faan <= 4) return 1 shl faan

    val m = faan - 4
    val base = if (m % 2 == 0) 2 else 3
    return base * (1 shl (3 + m / 2))
  }
}
