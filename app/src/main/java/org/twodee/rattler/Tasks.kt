package org.twodee.rattler

import android.os.AsyncTask
import androidx.room.Room
import java.lang.ref.WeakReference

class LoadDatabaseTask(adapter: SongAdapter) : AsyncTask<Unit, Unit, SongDatabase?>() {
  private val adapter = WeakReference(adapter)

  override fun doInBackground(vararg p0: Unit?): SongDatabase? {
    var database: SongDatabase? = null
    adapter.get()?.let {
      database = Room.databaseBuilder(it.context.applicationContext, SongDatabase::class.java, "songs").fallbackToDestructiveMigration().build()
    }
    return database
  }

  override fun onPostExecute(database: SongDatabase?) {
    adapter.get()?.let {
      it.database = database
    }
  }
}

class LoadSongsTask(private val database: SongDatabase,
                    private val adapter: SongAdapter) : AsyncTask<Unit, Unit, List<Song>>() {
  override fun doInBackground(vararg p0: Unit?): List<Song> {
    val songDao = database.songDao()
    return songDao.getAll()
  }

  override fun onPostExecute(songs: List<Song>) {
    adapter.songs = songs.toMutableList()
  }
}

class NewSongTask(private val database: SongDatabase,
                  private val song: Song) : AsyncTask<Unit, Unit, Unit>() {
  override fun doInBackground(vararg p0: Unit?) {
    song.id = database.songDao().insert(song)
  }
}

class UpdateSongTask(private val database: SongDatabase,
                     private val song: Song) : AsyncTask<Unit, Unit, Unit>() {
  override fun doInBackground(vararg p0: Unit?) {
    database.songDao().update(song)
  }
}

class DeleteSongTask(private val database: SongDatabase,
                     private val song: Song) : AsyncTask<Unit, Unit, Unit>() {
  override fun doInBackground(vararg p0: Unit?) {
    database.songDao().delete(song)
  }
}

class ClearDatabaseTask(private val database: SongDatabase) : AsyncTask<Unit, Unit, Unit>() {
  override fun doInBackground(vararg p0: Unit?) {
    database.clearAllTables()
  }
}
