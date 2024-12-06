package com.automacorp.model

enum class WindowStatus { OPENED, CLOSED}

data class WindowDto(
    val id: Long,
    var name: String,
    val roomName: String,
    val roomId: Long,
    val windowStatus: WindowStatus
)

data class WindowCommandDto(
    val name: String,
    val windowStatus: Long,
    val room_id: Long
)

data class WindowGetDto(
    val id: Long,
    val name: String,
    val windowStatus: Int,
    val roomId: Long
)

fun WindowGetDto.toWindowDto(roomName: String): WindowDto {
    return WindowDto(
        id = this.id,
        name = this.name,
        roomName = roomName,
        roomId = this.roomId,
        windowStatus = when (this.windowStatus) {
            1 -> WindowStatus.OPENED
            0 -> WindowStatus.CLOSED
            else -> throw IllegalArgumentException("Unknown window status: $windowStatus")
        }
    )
}