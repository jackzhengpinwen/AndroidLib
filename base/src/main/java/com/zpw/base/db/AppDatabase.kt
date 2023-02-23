package com.zpw.base.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [GZLL::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gzllDao(): GZLLDao
}