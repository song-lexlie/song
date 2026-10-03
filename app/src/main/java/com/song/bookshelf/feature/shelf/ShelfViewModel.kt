package com.song.bookshelf.feature.shelf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.song.bookshelf.core.data.local.ShelfWithCount
import com.song.bookshelf.core.data.repository.ShelfRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShelfViewModel @Inject constructor(
    private val shelfRepository: ShelfRepository,
) : ViewModel() {

    val shelves: StateFlow<List<ShelfWithCount>?> = shelfRepository.observeShelves()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    init {
        viewModelScope.launch { shelfRepository.ensureDefaultShelf() }
    }
}
