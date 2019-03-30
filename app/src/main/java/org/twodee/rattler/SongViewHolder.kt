package org.twodee.rattler

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView

class SongViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
  private val textView: TextView = view.findViewById(android.R.id.text1)

  var isActive: Boolean = false
    set(value) {
      field = value
      view.setBackgroundColor(if (value) Color.LTGRAY else Color.TRANSPARENT)
    }

  var song: Song = Song("", 100, "")
    set(value) {
      field = value
      textView.text = song.title
    }
}