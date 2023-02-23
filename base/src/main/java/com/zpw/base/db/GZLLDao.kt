package com.zpw.base.db

import androidx.room.*

@Dao
interface GZLLDao {
    @Query("SELECT * FROM gzll")
    fun getAll(): List<GZLL>

    @Query("SELECT * FROM gzll WHERE gzllId IN (:gzllIds)")
    fun loadAllByIds(gzllIds: IntArray): GZLL

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(gzll: List<GZLL>)

    @Delete
    fun delete(gzll: GZLL)
}