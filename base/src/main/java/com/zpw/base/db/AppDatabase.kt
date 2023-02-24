package com.zpw.base.db

import android.app.Application
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [GZLL::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gzllDao(): GZLLDao

    companion object {
        private var instance: AppDatabase? = null

        fun getInstance(): AppDatabase {
            return instance!!
        }

        fun init(context: Application) {
            instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "database-gzll"
                ).build().also { instance = it }
            }
        }
    }
}