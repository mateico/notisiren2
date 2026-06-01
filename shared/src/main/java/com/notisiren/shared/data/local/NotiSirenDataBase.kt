package com.notisiren.shared.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.notisiren.shared.data.local.dao.FilterDao
import com.notisiren.shared.data.local.entity.FilterEntity

@Database(
    entities = [FilterEntity::class],
    version = 1,
    exportSchema = false
)
abstract class NotiSirenDataBase(): RoomDatabase() {
    abstract fun filterDao(): FilterDao
}