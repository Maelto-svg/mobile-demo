package com.automacorp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.automacorp.model.RoomDto
import com.automacorp.service.RoomService
import com.automacorp.ui.theme.AutomacorpTheme

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
                        rooms = RoomService.ROOMS, // Utilise les données de RoomService
                        modifier = Modifier.padding(innerPadding),
                        onRoomClick = onRoomClick
                    )
                }
            }
        }
    }
}

@Composable
fun RoomList(rooms: List<RoomDto>, modifier: Modifier = Modifier, onRoomClick: (RoomDto) -> Unit) {
    LazyColumn(modifier = modifier.padding(16.dp)) {
        item {
            rooms.forEach { room ->
                Text(
                    text = room.name,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onRoomClick(room) }
                        .padding(vertical = 8.dp)
                )
            }
        }
    }
}



