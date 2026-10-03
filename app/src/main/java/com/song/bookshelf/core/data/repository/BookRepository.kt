package com.song.bookshelf.core.data.repository

import com.song.bookshelf.core.data.local.AppDatabase
import com.song.bookshelf.core.data.local.BookEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookRepository @Inject constructor(
    db: AppDatabase,
) {
    private val bookDao = db.bookDao()

    fun observeBook(id: String): Flow<BookEntity?> = bookDao.observeById(id)

    suspend fun getBook(id: String): BookEntity? = bookDao.getById(id)

    /** 같은 ISBN 의 책이 있으면 (소프트 삭제된 것 포함) 돌려준다. */
    suspend fun findByIsbn(isbn13: String): BookEntity? = bookDao.findByIsbn(isbn13)

    suspend fun save(book: BookEntity) {
        bookDao.upsert(book.copy(updatedAt = System.currentTimeMillis()))
    }
}
