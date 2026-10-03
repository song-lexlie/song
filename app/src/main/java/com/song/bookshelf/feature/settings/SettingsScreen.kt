package com.song.bookshelf.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.song.bookshelf.core.data.model.SpineDefault

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val spineDefault by viewModel.spineDefault.collectAsStateWithLifecycle()

    Scaffold(topBar = { TopAppBar(title = { Text("설정") }) }) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
        ) {
            SectionTitle("책등 이미지가 없는 책")
            Column(Modifier.selectableGroup()) {
                SpineDefault.entries.forEach { option ->
                    SpineOptionRow(
                        option = option,
                        selected = spineDefault == option,
                        onSelect = { viewModel.setSpineDefault(option) },
                    )
                }
            }

            HorizontalDivider(Modifier.padding(vertical = 8.dp))

            SectionTitle("API 키")
            viewModel.apiKeys.forEach { key ->
                ListItem(
                    headlineContent = { Text(key.name) },
                    trailingContent = {
                        Text(
                            if (key.configured) "설정됨" else "없음",
                            color = if (key.configured) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.error
                            },
                        )
                    },
                )
            }
            Text(
                "키는 프로젝트의 local.properties 에 넣고 다시 빌드하세요.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 4.dp),
    )
}

@Composable
private fun SpineOptionRow(option: SpineDefault, selected: Boolean, onSelect: () -> Unit) {
    val (title, description) = when (option) {
        SpineDefault.BLANK -> "빈 책등" to "회색 책등에 '사진 등록' 표시"
        SpineDefault.AUTO -> "자동 생성 책등" to "표지 대표색 배경에 제목을 세로로"
    }
    Row(
        Modifier
            .fillMaxWidth()
            .selectable(selected = selected, onClick = onSelect, role = Role.RadioButton)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = selected, onClick = null)
        Column(Modifier.padding(start = 16.dp)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(
                description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
