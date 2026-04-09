package denis.and.co.handshop.data.repository

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import denis.and.co.handshop.data.model.Review
import kotlinx.coroutines.tasks.await

class ReviewRepository {
    private val db = Firebase.firestore
    private val reviewsCollection = db.collection("reviews")

    suspend fun getReviewsForSeller(sellerId: String): List<Review> {
        return try {
            val snapshot = reviewsCollection
                .whereEqualTo("sellerId", sellerId)
                .get()
                .await()

            Log.d("FIREBASE_DATA", "Найдено документов: ${snapshot.size()}")
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Review::class.java)?.copy(id = doc.id)
            }

        } catch (ex: Exception) {
            Log.e("FIREBASE_ERROR", "Ошибка загрузки отзывов: ${ex.message}", ex)
            emptyList()
        }
    }

    suspend fun addReview(review: Review): Result<Boolean> {
        return try {
            reviewsCollection.add(review).await()
            Result.success(true)
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }

    suspend fun getReviewsSortedByAsc(sellerId: String): List<Review> {
        if(sellerId.isEmpty()) return emptyList()
        return try {
            val snapshot = reviewsCollection
                .whereEqualTo("sellerId", sellerId)
                .orderBy("selectedRate", Query.Direction.ASCENDING)
                .get()
                .await()

            Log.d("FIREBASE_DATA", "Найдено документов: ${snapshot.size()}")
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Review::class.java)?.copy(id = doc.id)
            }
        } catch (ex: Exception) {
            Log.e("FIREBASE_ERROR", "Ошибка загрузки отзывов: ${ex.message}", ex)
            emptyList()
        }
    }

    suspend fun getReviewsSortedByDesc(sellerId: String): List<Review> {
        if(sellerId.isEmpty()) return emptyList()
        return try {
            val snapshot = reviewsCollection
                .whereEqualTo("sellerId", sellerId)
                .orderBy("selectedRate", Query.Direction.DESCENDING)
                .get()
                .await()

            Log.d("FIREBASE_DATA", "Найдено документов: ${snapshot.size()}")
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Review::class.java)?.copy(id = doc.id)
            }
        } catch (ex: Exception) {
            Log.e("FIREBASE_ERROR", "Ошибка загрузки отзывов: ${ex.message}", ex)
            emptyList()
        }
    }

    suspend fun getReviewsSortedByGreaterDate(sellerId: String): List<Review> {
        if(sellerId.isEmpty()) return emptyList()
        return try {
            val snapshot = reviewsCollection
                .whereEqualTo("sellerId", sellerId)
                .orderBy("date", Query.Direction.ASCENDING)
                .get()
                .await()

            Log.d("FIREBASE_DATA", "Найдено документов: ${snapshot.size()}")
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Review::class.java)?.copy(id = doc.id)
            }
        } catch (ex: Exception) {
            Log.e("FIREBASE_ERROR", "Ошибка загрузки отзывов: ${ex.message}", ex)
            emptyList()
        }
    }

    suspend fun addReviewAndUpdateSeller(review: Review): Result<Unit> {
        return try {
            db.runTransaction { transaction ->
                val sellerRef = db.collection("sellers").document(review.sellerId ?: return@runTransaction)
                val sellerSnap = transaction.get(sellerRef)

                val currentCount = sellerSnap.getLong("reviewsCount") ?: 0
                val currentSum = sellerSnap.getDouble("ratingSum") ?: 0.0

                val newCount = currentCount + 1
                val newSum = currentSum + review.selectedRate
                val newRating = newSum / newCount

                val reviewRef = db.collection("reviews").document()
                transaction.set(reviewRef, review.copy(id = reviewRef.id))

                transaction.update(sellerRef, mapOf(
                    "reviewsCount" to newCount,
                    "ratingSum" to newSum,
                    "rate" to newRating
                ))
            }.await()
            Result.success(Unit)
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }
}