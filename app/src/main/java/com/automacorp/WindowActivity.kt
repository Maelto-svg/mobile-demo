package com.automacorp

import android.os.Bundle
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.automacorp.model.WindowDto
import com.automacorp.model.WindowStatus
import com.automacorp.model.WindowViewModel
import com.automacorp.ui.theme.AutomacorpTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

 class WindowActivity : ComponentActivity() {
        companion object {
            const val WINDOW_PARAM = "com.automacorp.window.id"
            const val ROOM_PARAM = "com.automacorp.room.name"
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            enableEdgeToEdge()

            val windowId = intent.getLongExtra(WINDOW_PARAM, -1L)
            val roomName = intent.getStringExtra(ROOM_PARAM)

            if (windowId == -1L) {
                Toast.makeText(this, "Invalid window ID", Toast.LENGTH_SHORT).show()
                finish()  // Close the activity if the ID is invalid
                return
            }

            val viewModel: WindowViewModel by viewModels()

            // Fetch window details when the activity is created
            if (roomName != null) {
                viewModel.find(windowId, roomName)
            }

            val onWindowSave: () -> Unit = {
                if (viewModel.window != null) {
                    val windowDto: WindowDto = viewModel.window as WindowDto
                    Log.d("Window Save", "$windowDto")
                    viewModel.updateWindow(windowDto.id, windowDto) { success ->
                        // Switch to the main thread before showing the toast
                        if (success) {
                            CoroutineScope(Dispatchers.Main).launch {
                                Toast.makeText(baseContext, "Window ${windowDto.name} was updated", Toast.LENGTH_LONG).show()
                            }
                            startActivity(Intent(baseContext, MainActivity::class.java))
                        } else {
                            CoroutineScope(Dispatchers.Main).launch {
                                Toast.makeText(baseContext, "Failed to update window", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            }

            setContent {
                AutomacorpTheme {
                    Scaffold(
                        topBar = { AutomacorpTopAppBar(
                            context = this,
                            title = "Windows",
                            returnAction = { finish() },
                        ) },
                        floatingActionButton = { WindowUpdateButton(onWindowSave) },
                        modifier = Modifier.fillMaxSize()) { innerPadding ->
                        WindowDetailScreen(
                            windowId = windowId,
                            viewModel = viewModel,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }


@Composable
fun WindowDetailScreen(
    windowId: Long,
    viewModel: WindowViewModel,
    modifier: Modifier = Modifier
) {
    // Check if the window is not null
    if (viewModel.window != null) {
        val windowDto = viewModel.window as WindowDto
        var windowName by remember { mutableStateOf(windowDto.name) }
        var windowStatus by remember { mutableStateOf(windowDto.windowStatus) }

        Column(modifier = modifier.padding(16.dp)) {
            // Editable Window Name
            TextField(
                value = windowName,
                onValueChange = {
                    windowName = it
                    viewModel.window!!.name = windowName
                },
                label = { Text(text = "Window Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Window Status Labels (Open / Closed)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Open label
                StatusLabel(
                    text = "Open",
                    isSelected = windowStatus == WindowStatus.OPENED,
                    onClick = { windowStatus = WindowStatus.OPENED }
                )
                // Closed label
                StatusLabel(
                    text = "Closed",
                    isSelected = windowStatus == WindowStatus.CLOSED,
                    onClick = { windowStatus = WindowStatus.CLOSED }
                )
            }
        }
    } else {
        // Show loading indicator or empty state if the window is not yet loaded
        CircularProgressIndicator()
    }
}


@Composable
fun StatusLabel(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier
            .clickable { onClick() }
            .padding(8.dp)
            .border(
                BorderStroke(1.dp, if (isSelected) Color.Blue else Color.Gray),
                shape = MaterialTheme.shapes.small
            )
            .padding(16.dp),
        color = if (isSelected) Color.Blue else Color.Gray
    )
}

@Composable
fun WindowUpdateButton(onClick: () -> Unit) {
    ExtendedFloatingActionButton(
        onClick = { onClick() },
        icon = {
            Icon(
                Icons.Filled.Done,
                contentDescription = stringResource(R.string.act_window_save),
            )
        },
        text = { Text(text = stringResource(R.string.act_window_save)) }
    )
}

@Preview(showBackground = true)
@Composable
fun WindowDetailScreenPreview() {
    AutomacorpTheme {
        WindowDetailScreen(windowId = 1L, viewModel = WindowViewModel()) // Preview with a dummy viewModel
    }
}
