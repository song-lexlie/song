package com.song.bookshelf.core.data.model

/** 두께 값을 어디서 얻었는지. 우선순위: ALADIN → YES24_RATIO → PAGES → DEFAULT, USER 는 사용자가 직접 수정. */
enum class ThicknessSource { ALADIN, YES24_RATIO, PAGES, DEFAULT, USER }

/** 책별 책등 표시 방식. DEFAULT 는 설정의 앱 전체 기본값([SpineDefault])을 따른다. */
enum class SpineMode { DEFAULT, BLANK, AUTO }

/** 책등 이미지가 없는 책의 앱 전체 기본 표시 방식. */
enum class SpineDefault { BLANK, AUTO }

enum class ShelfStyle { WOOD, WHITE, BLACK }

enum class SortMode { REGISTERED, TITLE, READ_DATE, CUSTOM }
