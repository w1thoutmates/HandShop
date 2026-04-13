package denis.and.co.handshop.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import denis.and.co.handshop.data.model.Seller
import kotlinx.coroutines.tasks.await

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
            firestore.collection("sellers")
                .document(userId)
                .update("likedProductIds", FieldValue.arrayUnion(productId))
                .await()

            Result.success(Unit)
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }

    suspend fun deleteFromLiked(userId: String, productId: String): Result<Unit> {
        return try {
            firestore.collection("sellers")
                .document(userId)
                .update("likedProductIds", FieldValue.arrayRemove(productId))
                .await()

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
}