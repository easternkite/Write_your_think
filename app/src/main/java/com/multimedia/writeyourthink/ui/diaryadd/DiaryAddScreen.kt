package com.multimedia.writeyourthink.ui.diaryadd

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimedia.writeyourthink.viewmodels.DiaryViewModel

const val ROUTE_ADD_DIARY = "route_add_diary"

@Composable
fun DiaryAddScreen(
    onNavigateBack: () -> Unit,
    onAddButtonClicked: () -> Unit,
    viewModel: DiaryViewModel = hiltViewModel()
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { TopAppBar(onNavigateBack, onAddButtonClicked) }
    ) { paddingValues ->
        Column(Modifier.padding(paddingValues)) {
            MultilineTextField()
        }
    }
}

@Composable
fun MultilineTextField() {
    var text by rememberSaveable { mutableStateOf("") }
    TextField(
        value = text,
        onValueChange = { text = it }, modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(10.dp)
            .border(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(8.dp))
    )
}

@Composable
@Preview
fun TextFieldPreview() {
    MultilineTextField()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    onNavigateBack: () -> Unit,
    onAddButtonClicked: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {},
        navigationIcon = { IconButton(onNavigateBack) { Icon(Icons.AutoMirrored.Default.ArrowBack, "Navigate Back") } },
        actions = { IconButton(onAddButtonClicked) { Icon(Icons.Filled.Add, "Add Note") } },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
@Preview
fun TopappBarPreview() {
    TopAppBar({}, {})
}