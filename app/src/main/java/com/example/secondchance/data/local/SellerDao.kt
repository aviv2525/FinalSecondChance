package com.example.secondchance.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.secondchance.data.model.Seller

@Dao
interface SellerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(sellers: List<Seller>)

    @Query("SELECT * FROM sellersList")
    fun getAllSellers(): LiveData<List<Seller>>

    @Query("SELECT * FROM sellers WHERE id = :id")
    suspend fun getSellerById(id: Int): Seller?
}
