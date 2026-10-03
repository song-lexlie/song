package com.song.bookshelf.core.data.repository

import androidx.room.withTransaction
import com.song.bookshelf.core.data.local.AppDatabase
import com.song.bookshelf.core.data.local.ShelfEntity
import com.song.bookshelf.core.data.local.ShelfItemEntity
import com.song.bookshelf.core.data.local.ShelfWithCount
import com.song.bookshelf.core.data.local.ShelvedBook
import com.song.bookshelf.core.data.model.ShelfStyle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShelfRepository @Inject constructor(
    private val db: AppDatabase,
) {
    private val shelfDao = db.shelfDao()
    private val itemDao = db.shelfItemDao()
    private val defaultShelfMutex = Mutex()

    fun observeShelves(): Flow<List<ShelfWithCount>> = shelfDao.observeShelvesWithCount()

    fun observeBooksInShelf(shelfId: String): Flow<List<ShelvedBook>> =
        itemDao.observeBooksInShelf(shelfId)

    /** 살아 있는 책장이 하나도 없으면 "내 책장"을 만든다. 여러 번 불려도 한 번만 생성. */
    suspend fun ensureDefaultShelf() {
        defaultShelfMutex.withLock {
            db.withTransaction<Unit> {
                if (shelfDao.countActive() == 0) {
                    shelfDao.insert(ShelfEntity(name = DEFAULT_SHELF_NAME, style = ShelfStyle.WOOD))
                }
            }
        }
    }

    suspend fun createShelf(name: String, style: ShelfStyle = ShelfStyle.WOOD): ShelfEntity =
        db.withTransaction {
            val shelf = ShelfEntity(
                name = name,
                style = style,
                position = (shelfDao.maxPosition() ?: -1) + 1,
            )
            shelfDao.insert(shelf)
            shelf
        }

    /** 책장과 그 안의 꽂힘 정보를 소프트 삭제한다. 책 자체는 남는다. */
    suspend fun deleteShelf(shelfId: String) {
        val now = System.currentTimeMillis()
        db.withTransaction<Unit> {
            itemDao.softDeleteByShelf(shelfId, now)
            shelfDao.softDelete(shelfId, now)
        }
    }

    /** 책을 책장 맨 뒤에 꽂는다. 이미 꽂혀 있으면 아무것도 하지 않는다. */
    suspend fun addBookToShelf(shelfId: String, bookId: String) {
        val now = System.currentTimeMillis()
        db.withTransaction<Unit> {
            val existing = itemDao.find(shelfId, bookId)
            if (existing != null && !existing.deleted) return@withTransaction
            val nextPosition = (itemDao.maxPosition(shelfId) ?: -1) + 1
            if (existing == null) {
                itemDao.insert(
                    ShelfItemEntity(shelfId = shelfId, bookId = bookId, position = nextPosition, addedAt = now)
                )
            } else {
                itemDao.update(
                    existing.copy(position = nextPosition, addedAt = now, updatedAt = now, deleted = false)
                )
            }
        }
    }

    suspend fun removeBookFromShelf(shelfId: String, bookId: String) {
        val now = System.currentTimeMillis()
        db.withTransaction<Unit> {
            val existing = itemDao.find(shelfId, bookId) ?: return@withTransaction
            if (!existing.deleted) itemDao.update(existing.copy(deleted = true, updatedAt = now))
        }
    }

    companion object {
        const val DEFAULT_SHELF_NAME = "내 책장"
    }
}
