package com.automacorp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.automacorp.model.RoomDto
import com.automacorp.service.RoomService
import com.automacorp.ui.theme.AutomacorpTheme
import com.automacorp.ui.theme.PurpleGrey80
import androidx.compose.foundation.lazy.items


class RoomListActivity : ComponentActivity() {
    companion object {
        const val ROOM_PARAM = "com.automacorp.room.attribute"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val onRoomClick: (RoomDto) -> Unit = { room ->
            val intent = Intent(this, RoomActivity::class.java).apply {
                putExtra(ROOM_PARAM, room.name)
            }
            startActivity(intent)
        }

        setContent {
            AutomacorpTheme {
                Scaffold(
                    topBar = { AutomacorpTopAppBar(
                        context = this,
                        title = "Room List",
                        returnAction = { finish() }, // Closes the current activity
                    ) },
                    modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RoomList(
                        modifier = Modifier.padding(innerPadding),
                        onRoomClick = onRoomClick
                    )
                }
            }
        }
    }
}

@Composable
fun RoomItem(room: RoomDto, modifier: Modifier = Modifier) {
    Card(colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(1.dp, PurpleGrey80)
    ) {
        Row(
            modifier = modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = room.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Target temperature : " + (room.targetTemperature?.toString() ?: "?") + "°",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(
                text = (room.currentTemperature?.toString() ?: "?") + "°",
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Right,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}


@Composable
fun RoomList(modifier: Modifier = Modifier, onRoomClick: (RoomDto) -> Unit) {
    LazyColumn(contentPadding = PaddingValues(4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.padding(16.dp)) {
        val rooms = RoomService.findAll()
        items(rooms, key = { it.id }) {
            RoomItem(
                room = it,
                modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onRoomClick(it) }
                        .padding(vertical = 8.dp)
            )
        }
    }
}






