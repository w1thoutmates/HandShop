package denis.and.co.handshop.di

import android.content.Context
import denis.and.co.handshop.data.repository.ImageRepository
import denis.and.co.handshop.data.repository.ProductRepository
import denis.and.co.handshop.data.repository.SellerRepository

object AppDependencies {
    val sellerRepository by lazy { SellerRepository() }
    val productRepository by lazy { ProductRepository() }

    lateinit var imageRepository: ImageRepository
        private set

    fun init(context: Context) {
        if (!::imageRepository.isInitialized) {
            imageRepository = ImageRepository(context)
        }
    }
}