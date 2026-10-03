package com.song.bookshelf.core.data.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.song.bookshelf.core.data.model.ShelfStyle
import com.song.bookshelf.core.data.model.SortMode
import com.song.bookshelf.core.data.model.SpineMode
import com.song.bookshelf.core.data.model.ThicknessSource
import java.util.UUID

// 모든 엔티티는 나중 클라우드 동기화를 위해 UUID id, updatedAt, deleted(소프트 삭제)를 가진다.

@Entity(
    tableName = "books",
    indices = [Index(value = ["isbn13"], unique = true)],
)
data class BookEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val isbn13: String?,
    val title: String,
    val author: String? = null,
    val publisher: String? = null,
    val pubDate: String? = null,
    val pageCount: Int? = null,
    val thicknessMm: Float,
    val thicknessSource: ThicknessSource,
    val heightMm: Float,
    val widthMm: Float? = null,
    val coverUrl: String? = null,
    val coverLocalPath: String? = null,
    val yes24SpineUrl: String? = null,
    /** 사용자가 업로드한 책등 사진 (앱 내부 저장소 경로). */
    val spineLocalPath: String? = null,
    /** 표지에서 추출한 대표색 (ARGB), 자동 생성 책등에 사용. */
    val spineColor: Int? = null,
    val spineMode: SpineMode = SpineMode.DEFAULT,
    /** 읽은 날짜 (LocalDate.toEpochDay). */
    val readDate: Long? = null,
    /** 별점 1~5. */
    val rating: Int? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = createdAt,
    val deleted: Boolean = false,
)

@Entity(tableName = "shelves")
data class ShelfEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val style: ShelfStyle = ShelfStyle.WOOD,
    val rowCount: Int = DEFAULT_ROW_COUNT,
    val rowWidthMm: Int = DEFAULT_ROW_WIDTH_MM,
    val rowHeightMm: Int = DEFAULT_ROW_HEIGHT_MM,
    val sortMode: SortMode = SortMode.REGISTERED,
    val sortDesc: Boolean = false,
    val position: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = createdAt,
    val deleted: Boolean = false,
) {
    companion object {
        const val DEFAULT_ROW_COUNT = 3
        const val DEFAULT_ROW_WIDTH_MM = 500
        const val DEFAULT_ROW_HEIGHT_MM = 280
    }
}

@Entity(
    tableName = "shelf_items",
    foreignKeys = [
        ForeignKey(
            entity = ShelfEntity::class,
            parentColumns = ["id"],
            childColumns = ["shelfId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = BookEntity::class,
            parentColumns = ["id"],
            childColumns = ["bookId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["shelfId", "position"]),
        Index(value = ["shelfId", "bookId"], unique = true),
        Index(value = ["bookId"]),
    ],
)
data class ShelfItemEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val shelfId: String,
    val bookId: String,
    val position: Int,
    val addedAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = addedAt,
    val deleted: Boolean = false,
)

/** 책장 목록 화면용: 책장 + 꽂힌 책 수. */
data class ShelfWithCount(
    @Embedded val shelf: ShelfEntity,
    val bookCount: Int,
)

/** 책장에 꽂힌 책 한 권: 책 정보 + 그 책장 안에서의 위치. */
data class ShelvedBook(
    @Embedded val book: BookEntity,
    val itemId: String,
    val position: Int,
    val addedAt: Long,
)
