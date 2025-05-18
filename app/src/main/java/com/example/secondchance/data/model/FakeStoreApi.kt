package com.example.secondchance.data.model

import retrofit2.http.GET
import retrofit2.http.Path

interface FakeStoreApi {

    @GET("products")
    suspend fun getAllProducts(): List<ApiProduct>

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: Int): ApiProduct
}