package denis.and.co.handshop.data.repository

import android.util.Log
import android.widget.Toast
import com.google.firebase.*
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import denis.and.co.handshop.MainActivity
import denis.and.co.handshop.data.enums.ProductStatus
import denis.and.co.handshop.data.model.Product
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.tasks.await

class ProductRepository {

    private val db = Firebase.firestore
    private val productsCollection = db.collection("products")

    suspend fun getProducts(): List<Product> {
        return try {
            val snapshot = productsCollection
                .whereEqualTo("status", ProductStatus.ACTIVE)
                .get()
                .await()

            Log.d("FIREBASE_DATA", "Найдено документов: ${snapshot.size()}")
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Product::class.java)?.copy(id = doc.id)
            }
        } catch (ex: Exception) {
            Log.e("FIREBASE_MAP_ERROR", "Ошибка маппинга: ", ex)
            emptyList()
        }
    }

    suspend fun getProductById(id: String): Result<Product?> {
        return try {
            val snapshot = Firebase.firestore
                .collection("products")
                .document(id)
                .get()
                .await()

            if (!snapshot.exists()) {
                Log.e("FIREBASE_ERROR", "Документ не найден")
                return Result.success(null)
            }

            val product = snapshot.toObject(Product::class.java)

            Result.success(product?.copy(id = snapshot.id))

        } catch (ex: Exception) {
            Log.e("FIREBASE_ERROR", "Ошибка: ", ex)
            Result.failure(ex)
        }
    }

    suspend fun saveProduct(product: Product): Result<Unit> {
        return try {
            productsCollection.add(product).await()
            Result.success(Unit)
        } catch (ex: Exception) {
            Log.e("FIREBASE_SAVE_ERROR", "Ошибка сохранения товара", ex)
            Result.failure(ex)
        }
    }

    suspend fun getProductsBySellerId(sellerId: String?, onlyActive: Boolean = true): List<Product> {
        if (sellerId == null) return emptyList()

        return try {
            var query: Query = productsCollection.whereEqualTo("sellerId", sellerId)
            if (onlyActive) {
                query = query.whereEqualTo("status", ProductStatus.ACTIVE)
            }
            val snapshot = query.get().await()

            Log.d("FIREBASE_DATA", "Найдено товаров для продавца $sellerId: ${snapshot.size()}")
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Product::class.java)?.copy(id = doc.id)
            }
        } catch (ex: Exception) {
            Log.e("FIREBASE_MAP_ERROR", "Ошибка загрузки товаров продавца: ", ex)
            emptyList()
        }
    }
}