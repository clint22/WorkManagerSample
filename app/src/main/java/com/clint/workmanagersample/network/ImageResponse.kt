package com.clint.workmanagersample.network

data class ImageResponse(
    val author: String,
    val download_url: String,
    val height: Int,
    val id: String,
    val url: String,
    val width: Int
)