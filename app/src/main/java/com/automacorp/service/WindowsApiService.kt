package com.automacorp.service

import com.automacorp.model.RoomCommandDto
import com.automacorp.model.RoomDto
import com.automacorp.model.WindowCommandDto
import com.automacorp.model.WindowDto
import com.automacorp.model.WindowGetDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface WindowsApiService {

    @GET("windows")
    fun findAll(id: Long): Call<List<WindowGetDto>>

    @GET("windows/{id}")
    fun find(@Path("id") id: Long): Call<WindowGetDto>

    @PUT("windows/{id}")
    fun updateWindow(@Path("id") id: Long, @Body window: WindowCommandDto): Call<WindowDto>

}