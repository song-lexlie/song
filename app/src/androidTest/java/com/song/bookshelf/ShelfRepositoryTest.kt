package com.song.bookshelf

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.song.bookshelf.core.data.local.AppDatabase
import com.song.bookshelf.core.data.local.BookEntity
import com.song.bookshelf.core.data.model.ThicknessSource
import com.song.bookshelf.core.data.repository.BookRepository
import com.song.bookshelf.core.data.repository.ShelfRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ShelfRepositoryTest {

    private lateinit var db: AppDatabase
    private lateinit var shelves: ShelfRepository
    private lateinit var books: BookRepository

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java,
        ).build()
        shelves = ShelfRepository(db)
        books = BookRepository(db)
    }

    @After
    fun tearDown() {
        db.close()
    }

    private suspend fun newBook(title: String): BookEntity {
        val book = BookEntity(
            isbn13 = null,
            title = title,
            thicknessMm = 20f,
            thicknessSource = ThicknessSource.DEFAULT,
            heightMm = 210f,
        )
        books.save(book)
        return book
    }

    @Test
    fun defaultShelfIsCreatedOnlyOnce() = runTest {
        (1..5).map { async { shelves.ensureDefaultShelf() } }.awaitAll()
        shelves.ensureDefaultShelf()

        val list = shelves.observeShelves().first()
        assertEquals(1, list.size)
        assertEquals(ShelfRepository.DEFAULT_SHELF_NAME, list.single().shelf.name)
    }

    @Test
    fun booksAreReturnedInPositionOrder() = runTest {
        val shelf = shelves.createShelf("테스트")
        val a = newBook("가")
        val b = newBook("나")
        val c = newBook("다")
        shelves.addBookToShelf(shelf.id, a.id)
        shelves.addBookToShelf(shelf.id, b.id)
        shelves.addBookToShelf(shelf.id, c.id)
        shelves.addBookToShelf(shelf.id, a.id) // 이미 있으면 무시

        val titles = shelves.observeBooksInShelf(shelf.id).first().map { it.book.title }
        assertEquals(listOf("가", "나", "다"), titles)
        assertEquals(3, shelves.observeShelves().first().single().bookCount)
    }

    @Test
    fun removedBookIsExcludedAndCanBeAddedBackAtTheEnd() = runTest {
        val shelf = shelves.createShelf("테스트")
        val a = newBook("가")
        val b = newBook("나")
        shelves.addBookToShelf(shelf.id, a.id)
        shelves.addBookToShelf(shelf.id, b.id)

        shelves.removeBookFromShelf(shelf.id, a.id)
        assertEquals(listOf("나"), shelves.observeBooksInShelf(shelf.id).first().map { it.book.title })

        shelves.addBookToShelf(shelf.id, a.id)
        assertEquals(listOf("나", "가"), shelves.observeBooksInShelf(shelf.id).first().map { it.book.title })
    }

    @Test
    fun deletingShelfKeepsBooksAndOtherShelves() = runTest {
        val first = shelves.createShelf("첫째")
        val second = shelves.createShelf("둘째")
        val a = newBook("가")
        shelves.addBookToShelf(first.id, a.id)
        shelves.addBookToShelf(second.id, a.id)

        shelves.deleteShelf(first.id)

        val remaining = shelves.observeShelves().first()
        assertEquals(listOf("둘째"), remaining.map { it.shelf.name })
        assertEquals(1, remaining.single().bookCount)
        assertNotNull(books.getBook(a.id))
        assertTrue(shelves.observeBooksInShelf(first.id).first().isEmpty())
    }
}
