package com.automacorp.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel


class RoomViewModel: ViewModel() {
    var room by mutableStateOf<RoomDto?>(null)
}