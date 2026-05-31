package denis.and.co.handshop.data.repository

import android.util.Log
import com.google.firebase.*
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import denis.and.co.handshop.data.enums.ProductStatus
import denis.and.co.handshop.data.model.Product
import denis.and.co.handshop.utils.SearchIndexer
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
            val docRef = if (product.id.isNotEmpty()) {
                productsCollection.document(product.id)
            } else {
                productsCollection.document()
            }

            val finalProduct = product.copy(id = docRef.id)
            docRef.set(finalProduct).await()

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
        if (query.isBlank()) return emptyList()

        val ngrams = SearchIndexer.createIndex(query, "", "", emptyList())
        if (ngrams.isEmpty()) return emptyList()

        return try {
            val snapshot = productsCollection
                .whereArrayContainsAny("searchIndex", ngrams.take(10))
                .get()
                .await()

            snapshot.documents.mapNotNull {
                it.toObject(Product::class.java)?.copy(id = it.id)
            }.filter { it.status == ProductStatus.ACTIVE }
        } catch (ex: Exception) {
            Log.e("FIREBASE_SEARCH_ERROR", "Ошибка поиска по индексу: ${ex.message}", ex)
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

    suspend fun updateImpressionsCount(productId: String) {
        try {
            val productRef = db.collection("products").document(productId)
            productRef.update("impressionsCount", FieldValue.increment(1)).await()
        } catch (ex: Exception) {
            Log.e("FIREBASE_ERROR", "Ошибка обновления показов: ${ex.message}")
        }
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

    suspend fun getProductsByCategory(category: String): List<Product> {
        return try {
            val snapshot = productsCollection
                .whereEqualTo("status", ProductStatus.ACTIVE)
                .whereEqualTo("category", category)
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

    fun logAllProductsData() {

        productsCollection
            .get()
            .addOnSuccessListener { documents ->

                if (documents.isEmpty) {
                    Log.d("FIREBASE_PRODUCTS", "Коллекция products пуста")
                    return@addOnSuccessListener
                }

                Log.d("FIREBASE_PRODUCTS", "========== PRODUCTS START ==========")

                documents.forEachIndexed { index, document ->

                    val builder = StringBuilder()

                    builder.appendLine("----- PRODUCT ${index + 1} -----")
                    builder.appendLine("Document ID: ${document.id}")

                    document.data.forEach { (key, value) ->
                        builder.appendLine("$key: $value")
                    }

                    builder.appendLine("----------------------------")

                    Log.d("FIREBASE_PRODUCTS", builder.toString())
                }

                Log.d("FIREBASE_PRODUCTS", "=========== PRODUCTS END ===========")
            }
            .addOnFailureListener { exception ->
                Log.e(
                    "FIREBASE_PRODUCTS",
                    "Ошибка при получении products",
                    exception
                )
            }
    }
}