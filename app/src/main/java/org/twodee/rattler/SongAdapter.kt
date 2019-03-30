package org.twodee.rattler

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

class SongAdapter(val onClick: (Song) -> Unit) : RecyclerView.Adapter<SongViewHolder>() {
  private var selectedHolder: SongViewHolder? = null

  private val songs = listOf(
    Song("A", 100, "4a4"),
    Song("B", 120, "4h4")
  )

  override fun getItemCount(): Int {
    return songs.size
  }

  override fun onCreateViewHolder(parent: ViewGroup, position: Int): SongViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
    val holder = SongViewHolder(view)

    view.setOnClickListener() {
      selectedHolder?.isActive = false
      selectedHolder = holder
      selectedHolder?.isActive = true

      onClick(songs[holder.adapterPosition])
    }

    return holder
  }

  override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
    holder.song = songs[position]
  }
}
