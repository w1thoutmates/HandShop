package denis.and.co.handshop.data.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import denis.and.co.handshop.data.model.DailyReach
import denis.and.co.handshop.data.model.Seller
import denis.and.co.handshop.utils.formatToStandard
import kotlinx.coroutines.tasks.await
import java.time.ZoneId
import java.util.Date

class SellerRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = com.google.firebase.auth.FirebaseAuth.getInstance()

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    suspend fun saveSeller(seller: Seller): Result<Unit> {
        return try {
            firestore.collection("sellers")
                .document(seller.id)
                .set(seller)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun checkIfProfileExists(userId: String): Boolean {
        return try {
            val document = firestore.collection("sellers")
                .document(userId)
                .get()
                .await()

            document.exists()
        } catch (ex: Exception) {
            false
        }
    }

    suspend fun getSeller(userId: String): Result<Seller> {
        return try {
            val document = firestore.collection("sellers")
                .document(userId)
                .get()
                .await()

            val seller = document.toObject(Seller::class.java)
            if (seller != null) Result.success(seller)
            else Result.failure(Exception("Not found"))
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }

    suspend fun addToLiked(userId: String, productId: String): Result<Unit> {
        return try {
            firestore.runTransaction { transaction ->
                val sellerRef = firestore.collection("sellers").document(userId)
                val productRef = firestore.collection("products").document(productId)

                transaction.update(sellerRef, "likedProductIds", FieldValue.arrayUnion(productId))
                transaction.update(productRef, "addedToLikedCount", FieldValue.increment(1))
            }.await()

            Result.success(Unit)
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }

    suspend fun deleteFromLiked(userId: String, productId: String): Result<Unit> {
        return try {
            firestore.runTransaction { transaction ->
                val sellerRef = firestore.collection("sellers").document(userId)
                val productRef = firestore.collection("products").document(productId)

                transaction.update(sellerRef, "likedProductIds", FieldValue.arrayRemove(productId))
                transaction.update(productRef, "addedToLikedCount", FieldValue.increment(-1))
            }.await()

            Result.success(Unit)
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }

    suspend fun isProductLikedBySellerId(userId: String, productId: String): Boolean {
        return try {
            val document = firestore.collection("sellers")
                .document(userId)
                .get()
                .await()

            if (document.exists()) {
                val likedProductIds = document.get("likedProductIds") as? List<String> ?: emptyList()
                likedProductIds.contains(productId)
            } else {
                false
            }
        } catch (ex: Exception) {
            false
        }
    }

    suspend fun getSellersByIds(sellerIds: List<String>): Map<String, Seller> {
        if (sellerIds.isEmpty()) return emptyMap()

        return try {
            val uniqueIds = sellerIds.distinct()
            val chunks = uniqueIds.chunked(30)
            val sellersMap = mutableMapOf<String, Seller>()

            for (chunk in chunks) {
                val snapshot = firestore.collection("sellers")
                    .whereIn(FieldPath.documentId(), chunk)
                    .get()
                    .await()

                snapshot.documents.forEach { doc ->
                    doc.toObject(Seller::class.java)?.let { seller ->
                        sellersMap[doc.id] = seller
                    }
                }
            }
            sellersMap
        } catch (e: Exception) {
            Log.e("FIREBASE_ERROR", "Ошибка при загрузке продавцов", e)
            emptyMap()
        }
    }

    suspend fun updateSelectedLocation(userId: String, location: String): Result<Unit> {
        return try {
            firestore.collection("sellers")
                .document(userId)
                .update("selectedLocation", location)
                .await()

            Result.success(Unit)
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }

    suspend fun updateCountClicksOnContacts(sellerId: String) {
        try {
            val sellerRef = firestore.collection("sellers").document(sellerId)
            sellerRef.update("countClicksOnContacts", FieldValue.increment(1)).await()
        } catch (ex: Exception) {
            Log.e("FIREBASE_ERROR", "Ошибка обновления количества кликов по кнопке \"связаться\": ${ex.message}")
        }
    }

    fun updateImpression(sellerId: String) {
        val today = Date().formatToStandard()

        val metricsRef = firestore.collection("sellers")
            .document(sellerId)
            .collection("daily_stats")
            .document(today)

        val data = mapOf(
            "impressions" to FieldValue.increment(1),
            "date" to today
        )

        metricsRef.set(data, SetOptions.merge())
    }

//    suspend fun getDailyStats(sellerId: String, limit: Int = 7): List<DailyReach> {
//        return try {
//            val snapshot = firestore.collection("sellers")
//                .document(sellerId)
//                .collection("daily_stats")
//                .orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
//                .limit(limit.toLong())
//                .get()
//                .await()
//
//            snapshot.toObjects(DailyReach::class.java).sortedBy { it.date }
//        } catch (ex: Exception) {
//            Log.e("FIREBASE_ERROR", "Ошибка загрузки статистики", ex)
//            emptyList()
//        }
//    }

    suspend fun getDailyStats(sellerId: String, days: Int): List<DailyReach> {
        return try {
            val fromDate = Date()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .minusDays(days.toLong())
                .toString()

            val snapshot = firestore.collection("sellers")
                .document(sellerId)
                .collection("daily_stats")
                .whereGreaterThanOrEqualTo("date", fromDate)
                .orderBy("date", Query.Direction.ASCENDING)
                .get()
                .await()

            snapshot.toObjects(DailyReach::class.java)
        } catch (ex: Exception) {
            Log.e("FIREBASE_ERROR", "Ошибка загрузки статистики", ex)
            emptyList()
        }
    }
}