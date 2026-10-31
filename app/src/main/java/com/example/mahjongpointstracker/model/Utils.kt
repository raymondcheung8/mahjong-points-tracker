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
}
