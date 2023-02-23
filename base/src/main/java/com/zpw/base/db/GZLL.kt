package com.zpw.base.db

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity
data class GZLL(
    @ColumnInfo(name = "date") val date: String?,
    @ColumnInfo(name = "price") val price: String?
)