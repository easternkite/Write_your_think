package com.multimedia.writeyourthink.ui.calendar

import android.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.github.sundeepk.compactcalendarview.domain.Event
import com.google.firebase.auth.FirebaseAuth
import com.multimedia.writeyourthink.R
import com.multimedia.writeyourthink.ui.login.ROUTE_LOGIN
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

const val ROUTE_CALENDAR = "calendar"
fun NavHostController.navigateToCalendar() {
    navigate(ROUTE_CALENDAR) { popUpTo(ROUTE_LOGIN) { inclusive = true } }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    onNavigateToLoginScreen: () -> Unit,
    countDate: Map<String, Int> = hashMapOf(),
    userName: String = "Name"
) {
    val context = LocalContext.current
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val backgroundColor = MaterialTheme.colorScheme.surface.toArgb()
    val currentDayBackgroundColor = MaterialTheme.colorScheme.primaryContainer.toArgb()
    val currentDayTextColor = MaterialTheme.colorScheme.onPrimaryContainer.toArgb()
    val selectedDayBackgroundColor = MaterialTheme.colorScheme.surfaceVariant.toArgb()
    val selectedDayTextColor = MaterialTheme.colorScheme.onSurfaceVariant.toArgb()
    val primaryColor = MaterialTheme.colorScheme.primary.toArgb()

    val countDescription = stringResource(R.string.frag3_2)
    val case = stringResource(R.string.cases)
    val calendarView = remember { CompactCalendarView(context) }
    val emptyDescription = stringResource(id = R.string.frag3_1)

    var calendarTitle by remember { mutableStateOf("Calendar") }
    var description by remember { mutableStateOf(emptyDescription) }
    val setDescription = { date: Date ->
        val selectedDate = dateFormat.format(date)
        val countOfSelectedDate = countDate[selectedDate] ?: 0

        description = if (countOfSelectedDate > 0) {
            "$countDescription : $countOfSelectedDate $case"
        } else {
            emptyDescription
        }
    }
    val setTitle = { date: Date ->
        val titleFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        calendarTitle = titleFormat.format(date)
    }

    LaunchedEffect(Unit) { setTitle(calendarView.firstDayOfCurrentMonth) }

    LaunchedEffect(countDate) {
        countDate.keys.forEach {
            val (year, month, day) = it.split("-").map(String::toInt)
            val calendar = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month - 1)
                set(Calendar.DAY_OF_MONTH, day)
            }
            val event = Event(primaryColor, calendar.timeInMillis, "")
            calendarView.addEvent(event)
        }

        calendarView.setListener(object : CompactCalendarView.CompactCalendarViewListener {
            override fun onDayClick(dateClicked: Date?) {
                dateClicked ?: return
                setDescription(dateClicked)
            }

            override fun onMonthScroll(firstDayOfNewMonth: Date?) {
                calendarView.removeAllEvents()
                firstDayOfNewMonth ?: return
                setDescription(firstDayOfNewMonth)
                calendarView.setUseThreeLetterAbbreviation(true)
                setTitle(firstDayOfNewMonth)
            }
        })
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    BasicText(
                        text = calendarTitle,
                        style = MaterialTheme.
                        typography.bodyLarge.copy(
                            MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.surface)) {
            AndroidView(
                factory = { calendarView },
                update = {
                    it.setCalendarBackgroundColor(backgroundColor)
                    it.setCurrentDayBackgroundColor(currentDayBackgroundColor)
                    it.setCurrentDayTextColor(currentDayTextColor)
                    it.setCurrentSelectedDayBackgroundColor(selectedDayBackgroundColor)
                    it.setCurrentSelectedDayTextColor(selectedDayTextColor)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(10.dp)
            ) {

                BasicText(
                    text = description,
                    modifier = Modifier.align(Alignment.TopStart),
                    style = MaterialTheme.typography.bodyLarge.copy(MaterialTheme.colorScheme.onSurfaceVariant)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(10.dp)
                        .align(Alignment.BottomCenter),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Absolute.SpaceBetween
                ) {

                    BasicText(text = userName, style = MaterialTheme.typography.bodyLarge.copy(MaterialTheme.colorScheme.onSurface))
                    Button(onClick = {
                        FirebaseAuth.getInstance().signOut()
                        onNavigateToLoginScreen()
                    }) {
                        BasicText(text = stringResource(id = R.string.btn_logout))
                    }
                }
            }
        }
    }

}

@Composable
@Preview
fun CalendarScreenPreview() {
    CalendarScreen(onNavigateToLoginScreen = {})
}