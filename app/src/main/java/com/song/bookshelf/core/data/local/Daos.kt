package com.song.bookshelf.core.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Upsert
    suspend fun upsert(book: BookEntity)

    @Query("SELECT * FROM books WHERE id = :id")
    suspend fun getById(id: String): BookEntity?

    @Query("SELECT * FROM books WHERE id = :id AND deleted = 0")
    fun observeById(id: String): Flow<BookEntity?>

    /** 소프트 삭제된 책도 찾는다(ISBN 은 유니크라 다시 등록할 때 되살려야 함). */
    @Query("SELECT * FROM books WHERE isbn13 = :isbn13 LIMIT 1")
    suspend fun findByIsbn(isbn13: String): BookEntity?
}

@Dao
interface ShelfDao {
    @Query(
        """
        SELECT s.*,
          (SELECT COUNT(*) FROM shelf_items i
             JOIN books b ON b.id = i.bookId
            WHERE i.shelfId = s.id AND i.deleted = 0 AND b.deleted = 0) AS bookCount
        FROM shelves s
        WHERE s.deleted = 0
        ORDER BY s.position, s.createdAt
        """
    )
    fun observeShelvesWithCount(): Flow<List<ShelfWithCount>>

    @Query("SELECT * FROM shelves WHERE id = :id")
    suspend fun getById(id: String): ShelfEntity?

    @Query("SELECT COUNT(*) FROM shelves WHERE deleted = 0")
    suspend fun countActive(): Int

    @Query("SELECT MAX(position) FROM shelves WHERE deleted = 0")
    suspend fun maxPosition(): Int?

    @Insert
    suspend fun insert(shelf: ShelfEntity)

    @Update
    suspend fun update(shelf: ShelfEntity)

    @Query("UPDATE shelves SET deleted = 1, updatedAt = :now WHERE id = :id")
    suspend fun softDelete(id: String, now: Long)
}

@Dao
interface ShelfItemDao {
    @Query(
        """
        SELECT b.*, i.id AS itemId, i.position AS position, i.addedAt AS addedAt
        FROM shelf_items i
        JOIN books b ON b.id = i.bookId
        WHERE i.shelfId = :shelfId AND i.deleted = 0 AND b.deleted = 0
        ORDER BY i.position
        """
    )
    fun observeBooksInShelf(shelfId: String): Flow<List<ShelvedBook>>

    /** 소프트 삭제된 항목도 찾는다((shelfId, bookId) 유니크라 다시 꽂을 때 되살려야 함). */
    @Query("SELECT * FROM shelf_items WHERE shelfId = :shelfId AND bookId = :bookId LIMIT 1")
    suspend fun find(shelfId: String, bookId: String): ShelfItemEntity?

    @Query("SELECT MAX(position) FROM shelf_items WHERE shelfId = :shelfId AND deleted = 0")
    suspend fun maxPosition(shelfId: String): Int?

    @Insert
    suspend fun insert(item: ShelfItemEntity)

    @Update
    suspend fun update(item: ShelfItemEntity)

    @Query("UPDATE shelf_items SET deleted = 1, updatedAt = :now WHERE shelfId = :shelfId AND deleted = 0")
    suspend fun softDeleteByShelf(shelfId: String, now: Long)
}
