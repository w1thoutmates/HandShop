package denis.and.co.handshop.data

import android.content.Context
import denis.and.co.handshop.data.repository.ComplaintRepository
import denis.and.co.handshop.data.repository.ImageRepository
import denis.and.co.handshop.data.repository.ProductRepository
import denis.and.co.handshop.data.repository.ReviewRepository
import denis.and.co.handshop.data.repository.SellerRepository
import denis.and.co.handshop.viewmodel.CatalogViewModel

object AppDependencies {
    val sellerRepository by lazy { SellerRepository() }
    val productRepository by lazy { ProductRepository() }
    val reviewsRepository by lazy { ReviewRepository() }
    val complaintRepository by lazy { ComplaintRepository() }

    var globalCatalogViewModel: CatalogViewModel? = null

    lateinit var imageRepository: ImageRepository
        private set

    fun init(context: Context) {
        if (!::imageRepository.isInitialized) {
            imageRepository = ImageRepository(context)
        }
    }
}