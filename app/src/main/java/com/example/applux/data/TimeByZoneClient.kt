package com.example.applux.data

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


const val BASE_URL = "https://timeapi.io/api/"
object TimeByZoneClient {

    private val worldTimeInterface : WorldTimeApiInterface

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        worldTimeInterface = retrofit.create(WorldTimeApiInterface::class.java)
    }


    fun getTime(timezone: String) : Call<WorldTimeModel>{
        return worldTimeInterface.getTimeByTimezone(timezone)
    }
}