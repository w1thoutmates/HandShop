package denis.and.co.handshop.data.repository

import android.util.Log
import com.google.firebase.Firebase
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

                val currentCount = when (val count = sellerSnap.get("reviewsCount")) {
                    is Long -> count
                    is Int -> count.toLong()
                    is Double -> count.toLong()
                    else -> 0L
                }

                val currentSum = when (val sum = sellerSnap.get("ratingSum")) {
                    is Double -> sum
                    is Long -> sum.toDouble()
                    is Int -> sum.toDouble()
                    else -> 0.0
                }

                val newCount = currentCount + 1
                val newSum = currentSum + review.selectedRate
                val newRating = newSum / newCount

                require(newRating <= 5.0) { "Некорректный рейтинг: $newRating" }

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
            Log.e("REVIEW_ERROR", "Ошибка: ${ex.message}", ex)
            Result.failure(ex)
        }
    }

    suspend fun recalculateSellerRating(sellerId: String): Result<Double> {
        return try {
            val reviews = getReviewsForSeller(sellerId)
            if (reviews.isEmpty()) {
                db.collection("sellers").document(sellerId).update(
                    mapOf(
                        "reviewsCount" to 0,
                        "ratingSum" to 0.0,
                        "rate" to 0.0
                    )
                ).await()
                return Result.success(0.0)
            }

            val sum = reviews.sumOf { it.selectedRate.toDouble() }
            val count = reviews.size
            val newRating = sum / count

            db.collection("sellers").document(sellerId).update(
                mapOf(
                    "reviewsCount" to count,
                    "ratingSum" to sum,
                    "rate" to newRating
                )
            ).await()

            Result.success(newRating)
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }
}