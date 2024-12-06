package com.automacorp.model

data class RoomDto(
    val id: Long,
    var name: String,
    val currentTemperature: Double?,
    val targetTemperature: Double?,
    val windows: List<WindowDto>,
)

data class RoomCommandDto(
    val name: String,
    val currentTemperature: Double?,
    val targetTemperature: Double?,
    val floor: Int = 1,
    // Set to the default building ID (useful when you have not created screens to manage buildings)
    val buildingId: Long = -10
)


data class RoomGetDto(
    val id: Long,
    val name: String,
    val floor: Int,
    val currentTemperature: Double,
    val targetTemperature: Double,
    val window: List<WindowGetDto> = emptyList()
)

fun RoomGetDto.toRoomDto(): RoomDto {
    return RoomDto(
        id = this.id,
        name = this.name,
        currentTemperature = this.currentTemperature,
        targetTemperature = this.targetTemperature,
        windows = this.window.map { it.toWindowDto(this.name) } // Transform windows
    )
}

