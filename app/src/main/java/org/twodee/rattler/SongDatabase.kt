package org.twodee.rattler

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Song::class], version = 3)
abstract class SongDatabase : RoomDatabase() {
  abstract fun songDao(): SongDao
}