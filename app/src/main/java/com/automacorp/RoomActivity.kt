package com.automacorp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.automacorp.RoomListActivity.Companion.ROOM_PARAM
import com.automacorp.model.RoomDto
import com.automacorp.model.RoomViewModel
import com.automacorp.model.WindowDto
import com.automacorp.model.WindowStatus
import com.automacorp.ui.theme.AutomacorpTheme
import com.automacorp.ui.theme.PurpleGrey80
import kotlin.math.round

class RoomActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val param = intent.getStringExtra(MainActivity.ROOM_PARAM)
        val viewModel: RoomViewModel by viewModels()
        if (param != null) {
            try {
                val roomId = param.toLong()
                viewModel.findRoom(roomId)
            } catch (e: NumberFormatException) {
                Log.e("RoomActivity", "Invalid room ID: $param", e)
                Toast.makeText(this, "Invalid room ID provided", Toast.LENGTH_LONG).show()
            }
        }

        val onRoomSave: () -> Unit = {
            if(viewModel.room != null) {
                val roomDto: RoomDto = viewModel.room as RoomDto
                viewModel.updateRoom(roomDto.id, roomDto)
                Toast.makeText(baseContext, "Room ${roomDto.name} was updated", Toast.LENGTH_LONG).show()
                startActivity(Intent(baseContext, MainActivity::class.java))
            }
        }

        val onWindowClick: (WindowDto) -> Unit = { window ->

            val intent = Intent(this, WindowActivity::class.java).apply {
                putExtra(
                    WindowActivity.WINDOW_PARAM,
                    window.id
                ) // Pass the window ID to the new activity
                putExtra(
                    WindowActivity.ROOM_PARAM,
                    viewModel.room?.name
                )
            }
            startActivity(intent)
        }
        setContent {
            AutomacorpTheme {
                Scaffold(
                    topBar = { AutomacorpTopAppBar(
                        context = this,
                        title = "Automacorp",
                        returnAction = { finish() }, // Closes the current activity
                    ) },
                    floatingActionButton = { RoomUpdateButton(onRoomSave) },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    if (viewModel.room != null) {
                        RoomDetail(viewModel, onWindowClick, Modifier.padding(innerPadding))
                    } else {
                        NoRoom(Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
}


@Composable
fun RoomDetail(model: RoomViewModel, onWindowClick: (WindowDto)-> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp)) {
        Text(
            text = stringResource(R.string.act_room_name),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = model.room?.name ?: "",
            onValueChange = { model.room = model.room?.copy(name = it) },
            label = { Text(text = stringResource(R.string.act_room_name)) },
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = stringResource(R.string.act_room_current_temperature),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = model.room?.currentTemperature.toString()+"°C",
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = stringResource(R.string.act_room_target_temperature),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Slider(
            value = model.room?.targetTemperature?.toFloat() ?: 18.0f,
            onValueChange = { newValue ->
                model.room = model.room?.copy(
                    targetTemperature = newValue.toDouble(),
                    windows = model.room?.windows ?: emptyList()  // Ensure windows is not null
                )
            },
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.secondary,
                activeTrackColor = MaterialTheme.colorScheme.secondary,
                inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            steps = 0,
            valueRange = 10f..28f
        )
        Text(text = (round((model.room?.targetTemperature ?: 18.0) * 10) / 10).toString()+"°C")

        model.room?.windows?.let { windows ->
            if (windows.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.act_room_windows),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
                windows.forEach { window ->
                    WindowItem(window = window, onClick = {
                        onWindowClick(window)
                    })
                }
            } else {
                Text(
                    text = stringResource(R.string.act_room_no_windows),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}


@Composable
fun WindowItem(window: WindowDto, onClick: (WindowDto) -> Unit) {
    // Card to wrap the window item
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(1.dp, PurpleGrey80)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .clickable { onClick(window) },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = window.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Status: ${if (window.windowStatus == WindowStatus.OPENED) "Opened" else "Closed"}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}


@Composable
fun NoRoom(modifier: Modifier = Modifier) {
    Log.d("NoRoom", "There is no Room")
    Column(
        modifier = modifier
            .fillMaxSize() // Ensure it takes the full available space
            .padding(16.dp) // Add inner padding
    ) {
        Text(
            text = stringResource(R.string.act_room_none),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(bottom = 30.dp)
        )
    }
}

@Composable
fun RoomUpdateButton(onClick: () -> Unit) {
    ExtendedFloatingActionButton(
        onClick = { onClick() },
        icon = {
            Icon(
                Icons.Filled.Done,
                contentDescription = stringResource(R.string.act_room_save),
            )
        },
        text = { Text(text = stringResource(R.string.act_room_save)) }
    )
}

//@Preview(showBackground = true)
//@Composable
//fun RoomDetailPreview() {
//    AutomacorpTheme {
//        RoomDetail(
//            roomDto = RoomDto(
//                id = 1,
//                name = "Living Room",
//                currentTemperature = 22.5,
//                targetTemperature = 24.0,
//                listOf(
//                    WindowDto(
//                    1,
//                    "Window 1",
//                    "Living Room",
//                    1,
//                    WindowStatus.OPENED
//                    )
//                )
//            )
//        )
//    }
//}

@Preview(showBackground = true)
@Composable
fun NoRoomPreview() {
    AutomacorpTheme {
        NoRoom()
    }
}