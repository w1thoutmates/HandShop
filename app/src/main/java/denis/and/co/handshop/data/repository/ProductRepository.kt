package denis.and.co.handshop.data.repository

import android.util.Log
import android.widget.Toast
import com.google.firebase.*
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import denis.and.co.handshop.MainActivity
import denis.and.co.handshop.data.enums.ProductStatus
import denis.and.co.handshop.data.model.Product
import denis.and.co.handshop.utils.SearchIndexer
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
            if (product.id.isNotEmpty()) {
                productsCollection.document(product.id).set(product).await()
            } else {
                productsCollection.add(product).await()
            }
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

    suspend fun searchProducts(query: String): List<Product> {
        if (query.length < 2) return getProducts()

        val trigrams = SearchIndexer.createIndex(query, "", "", emptyList())

        return try {
            val snapshot = productsCollection
                .whereEqualTo("status", ProductStatus.ACTIVE)
                .whereArrayContainsAny("searchIndex", trigrams.take(10))
                .get()
                .await()

            snapshot.documents.mapNotNull {
                it.toObject(Product::class.java)?.copy(id = it.id)
            }
        } catch (ex: Exception) {
            emptyList()
        }
    }

    suspend fun updateTagStats(userId: String, tags: List<String>) {
        val userRef = db.collection("sellers").document(userId)
        db.runTransaction { transaction ->
            val snapshot = transaction.get(userRef)
            val currentStats = snapshot.get("userTagStats") as? Map<String, Long> ?: emptyMap()
            val newStats = currentStats.toMutableMap()
            tags.forEach { tag ->
                newStats[tag] = (newStats[tag] ?: 0L) + 1
            }
            transaction.update(userRef, "userTagStats", newStats)
        }.await()
    }

    suspend fun getProductsByTags(tags: List<String>): List<Product> {
        if (tags.isEmpty()) return emptyList()

        return try {
            val limitedTags = tags.take(10)

            val snapshot = productsCollection
                .whereEqualTo("status", ProductStatus.ACTIVE)
                .whereArrayContainsAny("tags", limitedTags)
                .limit(20)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Product::class.java)?.copy(id = doc.id)
            }
        } catch (ex: Exception) {
            Log.e("FIREBASE_TAG_SEARCH", "Ошибка поиска по тегам: ", ex)
            emptyList()
        }
    }

    suspend fun updateProductViewsCount(productId: String) {
        val productRef = db.collection("products").document(productId)
        db.runTransaction { transaction ->
            val snapshot = transaction.get(productRef)
            val currentViews = snapshot.get("viewsCount") as? Long ?: 0
            val newViews = currentViews + 1
            transaction.update(productRef, "viewsCount", newViews)
        }.await()
    }

    suspend fun getLikedProducts(productIds: List<String>): List<Product> {
        if (productIds.isEmpty()) return emptyList()

        return try {
            val chunks = productIds.chunked(10)
            val allProducts = mutableListOf<Product>()

            for (chunk in chunks) {
                val snapshot = productsCollection
                    .whereIn(FieldPath.documentId(), chunk)
                    .get()
                    .await()

                val products = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Product::class.java)?.copy(id = doc.id)
                }
                allProducts.addAll(products)
            }

            allProducts
        } catch (ex: Exception) {
            Log.e("FIREBASE_LIKED_ERROR", "Ошибка загрузки избранного:", ex)
            emptyList()
        }
    }
}