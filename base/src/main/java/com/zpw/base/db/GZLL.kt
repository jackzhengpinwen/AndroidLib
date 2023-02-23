package com.zpw.base.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class GZLL(
    @PrimaryKey val gzllId: Int,
    @ColumnInfo(name = "date") val date: String?,
    @ColumnInfo(name = "price") val price: Double?
)