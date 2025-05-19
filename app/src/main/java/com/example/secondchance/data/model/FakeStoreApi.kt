package com.example.secondchance.data.model

import com.example.secondchance.data.model.ApiProduct
import retrofit2.Response
import retrofit2.http.*

interface FakeStoreApi {

    @GET("products")
    suspend fun getAllProducts(): List<ApiProduct>

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: Int): ApiProduct

    @DELETE("products/{id}")
    suspend fun deleteProduct(@Path("id") id: Int): Response<Unit>

    @POST("products")
    suspend fun addProduct(@Body product: ApiProduct): Response<ApiProduct>

    @PUT("products/{id}")
    suspend fun updateProduct(
        @Path("id") id: Int,
        @Body product: ApiProduct
    ): Response<ApiProduct>
}
