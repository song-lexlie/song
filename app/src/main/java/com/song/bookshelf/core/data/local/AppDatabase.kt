package com.song.bookshelf.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [BookEntity::class, ShelfEntity::class, ShelfItemEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun shelfDao(): ShelfDao
    abstract fun shelfItemDao(): ShelfItemDao

    companion object {
        const val NAME = "bookshelf.db"
    }
}
