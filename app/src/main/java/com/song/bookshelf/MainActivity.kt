package com.song.bookshelf

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.song.bookshelf.core.ui.theme.BookshelfTheme
import com.song.bookshelf.navigation.BookshelfAppUi
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            BookshelfTheme {
                BookshelfAppUi()
            }
        }
    }
}
