package com.example.mahjongpointstracker.leaderboard

import com.example.mahjongpointstracker.R
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize

//Create mutable 2D list of size 14 by 2
val xIndex = 14
val yIndex = 2

// Create and initilalise list with 0?
public val handList = MutableList(yIndex) { MutableList(xIndex) {0}  }


@Composable
fun InitMyHand() {
  // Filling handList with values for debug
// yIndex = 0; this is for x coodinates
// yIndex = 1; this is for y coodinates
// xIndex = c(0:13); this is for tile number 1 to 14

// 2D list coordinates correspond to tile to be drawn, e.g:
// 1st tile (x,y) = ([0,0],[1,0])
// 7th tile (x,y) = ([0,6],[1,6])

// This is making a bit more sense in my head now

// Will hard code the hand 123m456s789p999p11z

// 1st tile is 1m. 1m has the coords on the tilemap of (0,2)
// handList [0][0] = x coordinate of 1st tile
  handList[0][0] = 0;

  // handList [1][0] = y coordinate of 1st tile
  handList[1][0] = 2;

  // The following examples should now be clear without comments. Hopefully...
  // 2nd tile
  handList[0][1] = 1;
  handList[1][1] = 2;
  // 3rd tile
  handList[0][2] = 2;
  handList[1][2] = 2;
  // 4th tile
  handList[0][3] = 3;
  handList[1][3] = 0;
  // 5th tile
  handList[0][4] = 4;
  handList[1][4] = 0;
  // 6th tile
  handList[0][5] = 5;
  handList[1][5] = 0;
  // 7th tile
  handList[0][6] = 6;
  handList[1][6] = 1;
  // 8th tile
  handList[0][7] = 7;
  handList[1][7] = 1;
  // 9th tile
  handList[0][8] = 8;
  handList[1][8] = 1;
  // 10th tile
  handList[0][9] = 8;
  handList[1][9] = 1;
  // 11th tile
  handList[0][10] = 8;
  handList[1][10] = 1;
  // 12th tile
  handList[0][11] = 8;
  handList[1][11] = 1;
  // 13th tile (note to self, if we are using z to reference all honor tiles, maybe put all on the same row in the spritesheet?)
  handList[0][12] = 0;
  handList[1][12] = 5;
  // 14th tile
  handList[0][13] = 0;
  handList[1][13] = 5;
}

@Composable
fun HandDraw(playerHandList: MutableList<MutableList<Int>>) {

  // Print shows values. This is a good place to leave it I believe.
  // Next step now will be to re approach the canvas draw func.
  // I need to draw Images from the spritesheet that corespond to the 2Dlist representing the
  // player hand. They need to appear in a row on the screen.


  val sheet = ImageBitmap.imageResource(R.drawable.tilemapv1)

  // Return if parameters(?) are null in 2D list. NOTE still using hardcoded 2D list
  val hand = playerHandList.getOrNull(0) ?: return

  Canvas(
    modifier = Modifier
      .fillMaxSize()
  ) {
    // Loops 14 times to draw each tile.
    for (i in 0 until 14) {
      println("looptest$i")
      drawImage(
        image = sheet,
        // srcOffset is the top left coord of the tile on the tileMap
        srcOffset = IntOffset(240*handList[0][i],350*handList[1][i]),
        srcSize = IntSize(240,350),
        // dstOffset is the top left postion to draw this image (all 14 tiles) in Row
        dstOffset = IntOffset((50 * i), 0),
        dstSize = IntSize(50,73),
        filterQuality = FilterQuality.None
      )
    }
  }
}
