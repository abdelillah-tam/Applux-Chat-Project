package com.example.applux.data

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WorldTimeApiInterface {

    @GET("Time/current/zone")
    fun getTimeByTimezone(@Query("timeZone") timezone: String): Call<WorldTimeModel>

}