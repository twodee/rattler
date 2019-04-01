package org.twodee.rattler

import androidx.room.*

@Dao
interface SongDao {
  @Query("SELECT * FROM songs")
  fun getAll(): List<Song>

  @Insert
  fun insert(song: Song): Long

  @Update
  fun update(song: Song)

  @Delete
  fun delete(song: Song)
}