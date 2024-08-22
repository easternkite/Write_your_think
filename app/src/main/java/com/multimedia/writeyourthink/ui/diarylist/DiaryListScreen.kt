package com.multimedia.writeyourthink.ui.diarylist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.multimedia.writeyourthink.R
import com.multimedia.writeyourthink.Util.Constants.Companion.DOWN
import com.multimedia.writeyourthink.Util.Constants.Companion.UP
import com.multimedia.writeyourthink.models.Diary
import com.multimedia.writeyourthink.ui.login.ROUTE_LOGIN
import com.multimedia.writeyourthink.viewmodels.DiaryViewModel

const val ROUTE_DIARY_LIST = "route_diary_list"
fun NavHostController.navigateToDiaryList() {
    navigate(ROUTE_DIARY_LIST) { popUpTo(ROUTE_LOGIN) { inclusive = true } }
}

@Composable
fun DiaryListScreen(
    onAddDiaryClick: () -> Unit,
    viewModel: DiaryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            MainTopBar(
                onDateDown = { viewModel.dateUpDown(DOWN)},
                onDateUp = { viewModel.dateUpDown(UP) },
                title = uiState.selectedDateTime
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddDiaryClick) {
                Icon(Icons.Filled.Edit, contentDescription = "Add Diary")
            }
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            items(uiState.filteredByDate, key = { it.date + it.diaryDate + it.contents }) {
                DiaryListCell(it)
            }
        }
    }
}

@Composable
fun DiaryListCell(
    diary: Diary,
    modifier: Modifier = Modifier
) {
    Column {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(10.dp)
        ) {
            BasicText(
                diary.contents,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            BasicText(
                diary.date,
                style = MaterialTheme.typography.bodySmall
            )
        }
        HorizontalDivider(Modifier.padding(horizontal = 10.dp))
    }

}

@Composable
@Preview
fun DiaryListCellPreview() {
    val mockDiary = Diary(
        contents = "Test Contents",
        date = "2024-08-12"
    )
    DiaryListCell(mockDiary)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(
    onDateUp: () -> Unit,
    onDateDown: () -> Unit,
    title: String = stringResource(R.string.date)
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary
        ),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onDateDown) {
                    BasicText(
                        text = stringResource(R.string.btn_previous),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Spacer(Modifier.width(30.dp))
                BasicText(
                    title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(Modifier.width(30.dp))
                IconButton(onDateUp) {
                    BasicText(
                        text = stringResource(R.string.btnNext),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    )
}