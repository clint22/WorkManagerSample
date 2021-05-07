package com.clint.workmanagersample.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface Service {

    @GET("id/{id}/info")
    suspend fun getImage(
        @Path("id") id: String
    ) : Response<ImageResponse>
}

