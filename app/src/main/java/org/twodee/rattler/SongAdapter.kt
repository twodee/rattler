package org.twodee.rattler

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

class SongAdapter(val context: Context) : RecyclerView.Adapter<SongViewHolder>() {
  private var selectedIndex: Int = RecyclerView.NO_POSITION
  private var recyclerView: RecyclerView? = null

  var onSelect: (Song) -> Unit = {}
  var onNothingSelected: () -> Unit = {}

  val isSongSelected: Boolean
    get() = selectedIndex != RecyclerView.NO_POSITION

  var songs: MutableList<Song> = mutableListOf(
    Song("Mary Had", 120, "4c4 4c4 4g4 4g4 4a4 4a4 2g4"),
    Song("Alex", 500, "4d5 32p 4d5 2d6 2a5 4p 2g#5 2g5 2f5 4d5 4f5 4g5 4c5 32p 4c5 2d6 2a5 4p 2g#5 2g5 2f5 4d5 4f5 4g5 4b4 32p 4b4 2d6 2a5 4p 2g#5 2g5 2f5 4d5 4f5 4g5 4a#4 32p 4a#4 2d6 2a5 4p 2g#5 2g5 2f5 4d5 4f5 4g5 2f5 32p 4f5 32p5 2f5 32p 2f5 32p 2f5 2d5 32p 2d5 4p 2d5 4f5 32p 4f5 32p 4f5 32p 2f5 2g5 2g#5 4g5 4f5 4d5 4f5 2g5 4p 2f5 32p 4f5 32p 2f5 2g5 2g#5 2a5 2c6 2a5 4p 2d6 32p 2d6 32p 4d6 4a5 4d6 1c6 1c6"),
    Song("Ollie", 200, "4c4 4e4 4g4 4c5 4e5 4c5 4e5 4g5 4e5 4g5 2c6"),
    Song("Tyler", 100, "16d5 16c#5 4d5 16d5 16c#5 4d5 16d5 16c#5 8e5 8d5 8c#5 4h4 16h4 16a4 4h4 16h4 16a4 4h4 16a4 16h4 8c#5 8d5 8a4 4h4"),
    Song("James", 500, "g4 g4 a5 g4 g4 g4 a5 g4 g4 g4 a5 g4 g4 g4 a5 g4 e4 e4 f4 e4 e4 e4 f4 e4 e4 e4 f4 e4 e4 e4 f4 e4 c4 c4 a5 c4 c4 c4 a5 c4 c4 c4 a5 c4 c4 c4 a5 c4")
  )
    set(value) {
      field = value
      notifyDataSetChanged()
    }

  override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
    super.onAttachedToRecyclerView(recyclerView)
    this.recyclerView = recyclerView
  }

  override fun getItemCount(): Int {
    return songs.size
  }

  override fun onCreateViewHolder(parent: ViewGroup, position: Int): SongViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.item_song, parent, false)
    val holder = SongViewHolder(view)

    view.setOnClickListener() {
      selectIndex(holder.adapterPosition)
    }

    return holder
  }

  override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
    holder.song = songs[position]
    holder.isActive = position == selectedIndex
  }

  // I need this only override change animation when a song is edited. I want the change animation
  // when the selection changes, but not when the song state is modified.
  override fun onBindViewHolder(holder: SongViewHolder, position: Int, payloads: MutableList<Any>) {
    if (payloads.isEmpty()) {
      onBindViewHolder(holder, position)
    } else {
      holder.song = songs[position]
    }
  }

  private fun selectIndex(i: Int) {
    if (i == selectedIndex) return

    val oldSelectedIndex = selectedIndex
    selectedIndex = i

    notifyItemChanged(oldSelectedIndex)
    notifyItemChanged(selectedIndex)

    if (selectedIndex == RecyclerView.NO_POSITION) {
      onNothingSelected()
    } else {
      recyclerView?.scrollToPosition(i)
      onSelect(songs[i])
    }
  }
}
