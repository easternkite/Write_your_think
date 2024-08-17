package com.multimedia.writeyourthink.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.multimedia.writeyourthink.R
import com.multimedia.writeyourthink.ui.screens.DiaryAddScreen
import com.multimedia.writeyourthink.ui.screens.DiaryListScreen
import com.multimedia.writeyourthink.ui.screens.ROUTE_ADD_DIARY
import com.multimedia.writeyourthink.ui.screens.ROUTE_DIARY_LIST
import dagger.hilt.android.AndroidEntryPoint
import org.checkerframework.common.subtyping.qual.Bottom

@AndroidEntryPoint
class ComposeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var selectedIndex by rememberSaveable { mutableIntStateOf(0) }
            val navController = rememberNavController()
            WytTheme {
                Scaffold(
                    bottomBar = {
                        BottomBar(selectedIndex) { item, index ->
                            navController.navigate(item.route)
                            selectedIndex = index
                        }
                    }
                ) { paddingValues ->
                    Surface(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                        NavHost(
                            navController,
                            startDestination = ROUTE_DIARY_LIST
                        ) {
                            composable(ROUTE_DIARY_LIST) {
                                DiaryListScreen(onAddDiaryClick = {
                                    navController.navigate(ROUTE_ADD_DIARY)
                                })
                            }

                            composable(ROUTE_ADD_DIARY) {
                                DiaryAddScreen(
                                    onNavigateBack = {
                                        navController.navigateUp()
                                    },
                                    onAddButtonClicked = {

                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomBar(
    selectedIndex: Int,
    onItemClicked: (item: BottomNavItem, index: Int) -> Unit
) {
    NavigationBar {
        BottomNavItem.entries.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = index == selectedIndex,
                label = { BasicText(stringResource(item.titleResId)) },
                icon = { Icon(item.icon, contentDescription = stringResource(item.titleResId)) },
                onClick = { onItemClicked(item, index) }
            )
        }
    }
}

enum class BottomNavItem(
    val titleResId: Int, val icon: ImageVector, val route: String
) {
    List(R.string.list, Icons.AutoMirrored.Filled.List, ROUTE_DIARY_LIST),
    Calendar(R.string.calendar, Icons.Filled.CalendarMonth, ROUTE_DIARY_LIST)
}

@Composable
@Preview
fun GreetingPreview() {
    Text("Hello, World!!")
}