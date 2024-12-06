package com.automacorp.model

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.automacorp.service.ApiServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


class WindowViewModel: ViewModel() {
    var window by mutableStateOf<WindowDto?>(null)
    val windowsState = MutableStateFlow(WindowList())

    fun findAll(roomId: Long) {
        viewModelScope.launch(context = Dispatchers.IO) {
            runCatching {
                // First, fetch the room details
                val roomResponse = ApiServices.roomsApiService.findById(roomId).execute()
                val room = roomResponse.body()

                // Then, fetch the windows for the room
                val windowsResponse = ApiServices.windowsApiService.findAll(roomId).execute()
                val windows = windowsResponse.body() ?: emptyList()

                // Update the UI with the windows and room name
                windowsState.value = WindowList(
                    windows.map { window -> window.toWindowDto( roomName = room?.name ?: "Unknown Room") }
                )
            }
                .onFailure {
                    it.printStackTrace()
                    windowsState.value = WindowList(emptyList(), it.stackTraceToString()) // Provide error details
                }
        }
    }

    fun find(id: Long, name: String){
        viewModelScope.launch(context = Dispatchers.IO) {
            runCatching { ApiServices.windowsApiService.find(id).execute() }
                .onSuccess {
                    window = it.body()?.toWindowDto(name)
                }
                .onFailure {
                    it.printStackTrace()
                    window = null
                }
        }
    }

    fun updateWindow(id: Long, windowDto: WindowDto, callback: (Boolean) -> Unit) {
        val command = WindowCommandDto(
            name = windowDto.name,
            windowStatus = when (windowDto.windowStatus) {
                WindowStatus.OPENED -> 1 // 1 for OPENED
                WindowStatus.CLOSED -> 0  // 0 for CLOSED
            },
            room_id = windowDto.roomId
        )
        Log.d("Window Save", "$command")
        viewModelScope.launch(context = Dispatchers.IO) {
            runCatching { ApiServices.windowsApiService.updateWindow(id, command).execute() }
                .onSuccess {
                    // If successful, update the window object
                    window = it.body()
                    callback(true) // Call the callback with success status
                }
                .onFailure {
                    it.printStackTrace()
                    window = null
                    callback(false) // Call the callback with failure status
                }
        }
    }


}