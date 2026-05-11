package denis.and.co.handshop.utils

import com.google.firebase.firestore.FirebaseFirestore
import denis.and.co.handshop.data.model.Product
import denis.and.co.handshop.data.model.Seller
import kotlinx.coroutines.tasks.await

object TestDataHelper {

    private val firestore = FirebaseFirestore.getInstance()

    private const val TEST_SELLER_ID = "metrics_test_seller"

    suspend fun createOrReplaceTestData() {

        deleteOldTestData()

        val seller = Seller(
            id = TEST_SELLER_ID,
            sellerName = "Metrics Test Seller",
            profileImage = "https://storage.ghost.io/c/fb/a8/fba8736e-797b-43bb-bcba-040fb8dcfebb/content/images/2020/03/Screen_Shot_2020-03-10_at_12.45.32_PM.jpg",
            coverImageUrl = "https://media.istockphoto.com/id/1451587807/ru/%D0%B2%D0%B5%D0%BA%D1%82%D0%BE%D1%80%D0%BD%D0%B0%D1%8F/%D0%B2%D0%B5%D0%BA%D1%82%D0%BE%D1%80-%D0%B7%D0%BD%D0%B0%D1%87%D0%BA%D0%B0-%D0%BF%D1%80%D0%BE%D1%84%D0%B8%D0%BB%D1%8F-%D0%BF%D0%BE%D0%BB%D1%8C%D0%B7%D0%BE%D0%B2%D0%B0%D1%82%D0%B5%D0%BB%D1%8F-%D0%B0%D0%B2%D0%B0%D1%82%D0%B0%D1%80-%D0%B8%D0%BB%D0%B8-%D0%B7%D0%BD%D0%B0%D1%87%D0%BE%D0%BA-%D1%87%D0%B5%D0%BB%D0%BE%D0%B2%D0%B5%D0%BA%D0%B0-%D1%84%D0%BE%D1%82%D0%BE%D0%B3%D1%80%D0%B0%D1%84%D0%B8%D1%8F-%D0%BF%D1%80%D0%BE%D1%84%D0%B8%D0%BB%D1%8F.jpg?s=612x612&w=0&k=20&c=6Wt7tVZi20iTGl7QzOEjzdWznig3MD5IYs9woVB9YoA="
        )

        firestore.collection("sellers")
            .document(TEST_SELLER_ID)
            .set(seller)
            .await()

        val products = listOf(
            Product(
                id = "metrics_product_1",
                title = "Яблоки",
                sellerId = TEST_SELLER_ID,
                category = "Другое",
                cost = 125000
            ),
            Product(
                id = "metrics_product_2",
                title = "Chair chair armchair",
                sellerId = TEST_SELLER_ID,
                category = "Дом и интерьер",
                cost = 118000
            )
        )

        products.forEach { product ->
            firestore.collection("products")
                .document(product.id)
                .set(product)
                .await()
        }
    }

    private suspend fun deleteOldTestData() {

        firestore.collection("sellers")
            .document(TEST_SELLER_ID)
            .delete()
            .await()

        listOf(
            "metrics_product_1",
            "metrics_product_2"
        ).forEach { productId ->
            firestore.collection("products")
                .document(productId)
                .delete()
                .await()
        }
    }
}