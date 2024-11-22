package com.automacorp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.automacorp.model.RoomDto
import com.automacorp.model.WindowDto
import com.automacorp.model.WindowStatus
import com.automacorp.service.RoomService
import com.automacorp.ui.theme.AutomacorpTheme
import kotlin.math.round

class RoomActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val param = intent.getStringExtra(MainActivity.ROOM_PARAM)
        val room = RoomService.findByNameOrId(param)

        setContent {
            AutomacorpTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (room != null) {
                        RoomDetail(room,Modifier.padding(innerPadding))
                    } else {
                        NoRoom(Modifier.padding(innerPadding))
                    }

                }
            }
        }
    }
}


@Composable
fun RoomDetail(roomDto: RoomDto, modifier: Modifier = Modifier) {
    var room by remember { mutableStateOf(roomDto) }
    Column(modifier = modifier.padding(16.dp)) {
        Text(
            text = stringResource(R.string.act_room_name),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            room.name,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { room = room.copy(name = it) },
            placeholder = { Text(stringResource(R.string.act_room_name)) },
        )
        Text(
            text = stringResource(R.string.act_room_current_temperature),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = room.currentTemperature.toString()+"°C",
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = stringResource(R.string.act_room_target_temperature),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Slider(
            value = room.targetTemperature?.toFloat() ?: 18.0f,
            onValueChange = { room = room.copy(targetTemperature = it.toDouble()) },
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.secondary,
                activeTrackColor = MaterialTheme.colorScheme.secondary,
                inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            steps = 0,
            valueRange = 10f..28f
        )
        Text(text = (round((room.targetTemperature ?: 18.0) * 10) / 10).toString()+"°C")
    }

}

@Composable
fun NoRoom(modifier: Modifier = Modifier){
    Text(
        text= stringResource(R.string.act_room_none),
        style = MaterialTheme.typography.labelSmall,
        modifier = Modifier.padding(bottom = 4.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun RoomDetailPreview() {
    AutomacorpTheme {
        RoomDetail(
            roomDto = RoomDto(
                id = 1,
                name = "Living Room",
                currentTemperature = 22.5,
                targetTemperature = 24.0,
                listOf(
                    WindowDto(
                    1,
                    "Window 1",
                    "Living Room",
                    1,
                    WindowStatus.OPENED
                    )
                )
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NoRoomPreview() {
    AutomacorpTheme {
        NoRoom()
    }
}