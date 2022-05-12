package com.kareemdev.dicodingstory.data.remote.response

import com.google.gson.annotations.SerializedName

data class UploadResponse (

    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)
