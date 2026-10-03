package com.song.bookshelf.feature.shelf

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.song.bookshelf.core.data.local.ShelfWithCount
import com.song.bookshelf.core.data.model.ShelfStyle

// 프롬프트 1 단계: 책장 목록과 권수만 보여준다. 실제 책장 그림은 프롬프트 5에서.

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShelfScreen(viewModel: ShelfViewModel = hiltViewModel()) {
    val shelves by viewModel.shelves.collectAsStateWithLifecycle()

    Scaffold(topBar = { TopAppBar(title = { Text("내 책장") }) }) { padding ->
        val list = shelves
        if (list == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(list, key = { it.shelf.id }) { ShelfRow(it) }
            }
        }
    }
}

@Composable
private fun ShelfRow(item: ShelfWithCount) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(item.shelf.name, style = MaterialTheme.typography.titleMedium)
            Text(
                "${item.shelf.style.label} · ${item.shelf.rowCount}칸 · ${item.bookCount}권",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private val ShelfStyle.label: String
    get() = when (this) {
        ShelfStyle.WOOD -> "원목"
        ShelfStyle.WHITE -> "화이트"
        ShelfStyle.BLACK -> "블랙"
    }
