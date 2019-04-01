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

  var database: SongDatabase? = null
    set(value) {
      field = value
      value?.let {
        LoadSongsTask(it, this).execute()
      }
    }

  init {
    LoadDatabaseTask(this).execute()
  }

  val isSongSelected: Boolean
    get() = database != null && selectedIndex != RecyclerView.NO_POSITION

  var songs: MutableList<Song> = mutableListOf()
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

  fun insert() {
    if (database != null) {
      val song = Song("Untitled", 100, "")
      songs.add(song)
      selectIndex(songs.size - 1)
      notifyItemInserted(songs.size - 1)

      NewSongTask(database!!, song).execute()
    }
  }

  fun clear() {
    if (database != null) {
      songs.clear()
      notifyDataSetChanged()
      ClearDatabaseTask(database!!).execute()
      onNothingSelected()
    }
  }

  fun update() {
    if (isSongSelected) {
      notifyItemChanged(selectedIndex, true)
      UpdateSongTask(database!!, songs[selectedIndex]).execute()
    }
  }

  fun delete() {
    if (isSongSelected) {
      val song = songs.removeAt(selectedIndex)
      selectIndex(RecyclerView.NO_POSITION)
      notifyItemRemoved(selectedIndex)
      DeleteSongTask(database!!, song).execute()
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
