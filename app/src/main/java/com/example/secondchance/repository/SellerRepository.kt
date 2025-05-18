package com.example.secondchance.repository

import com.example.secondchance.data.model.Seller
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class SellerRepository {

    private val db = Firebase.firestore
    private val sellersCollection = db.collection("sellers")

    fun getAllSellers(onResult: (List<Seller>) -> Unit) {
        sellersCollection.get()
            .addOnSuccessListener { result ->
                val sellers = result.documents.mapNotNull { it.toObject(Seller::class.java) }
                onResult(sellers)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    fun addSeller(seller: Seller, onComplete: (Boolean) -> Unit) {
        sellersCollection.add(seller)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }
}