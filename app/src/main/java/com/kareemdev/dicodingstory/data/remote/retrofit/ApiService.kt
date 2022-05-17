package com.kareemdev.dicodingstory.data.remote.retrofit

import com.kareemdev.dicodingstory.data.remote.response.LoginResponse
import com.kareemdev.dicodingstory.data.remote.response.RegisterResponse
import com.kareemdev.dicodingstory.data.remote.response.StoryResponse
import com.kareemdev.dicodingstory.data.remote.response.UploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("login")
    suspend fun userLogin(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse


    @FormUrlEncoded
    @POST("register")
    suspend fun userRegister(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse


    @GET("stories")
    suspend fun getAllStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int?,
        @Query("size") size: Int?,
        @Query("location") location: Int?
    ): StoryResponse

    @Multipart
    @POST("stories")
    suspend fun uploadImage(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody?,
        @Part("lon") lon: RequestBody?
    ): UploadResponse
}