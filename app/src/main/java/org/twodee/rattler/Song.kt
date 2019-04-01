package org.twodee.rattler

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.File
import java.io.FileOutputStream
import java.io.PrintStream

@Entity(tableName = "songs")
class Song(
  var title: String,
  @ColumnInfo(name = "beats_per_minute") var beatsPerMinute: Int,
  var notes: String
) {

  @PrimaryKey(autoGenerate = true) var id: Long = 0

  private fun toRtttl() = "$title:d=4,o=5,b=$beatsPerMinute:${notes.split("\\s+".toRegex()).joinToString(",")}"

  fun write(file: File) {
    PrintStream(FileOutputStream(file)).apply {
      println(toRtttl())
      close()
    }
  }
}