package org.twodee.rattler

import java.io.File
import java.io.FileOutputStream
import java.io.PrintStream

class Song(var title: String, var beatsPerMinute: Int, var notes: String) {
  private fun toRtttl() = "$title:d=4,o=5,b=$beatsPerMinute:${notes.split("\\s+".toRegex()).joinToString(",")}"

  fun write(file: File) {
    PrintStream(FileOutputStream(file)).apply {
      println(toRtttl())
      close()
    }
  }
}