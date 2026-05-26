package denis.and.co.handshop.viewmodel

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import denis.and.co.handshop.data.model.Product
import denis.and.co.handshop.data.repository.ImageRepository
import denis.and.co.handshop.data.repository.ProductRepository
import denis.and.co.handshop.data.repository.SellerRepository
import denis.and.co.handshop.utils.SearchIndexer
import kotlinx.coroutines.launch

class CreateProductViewModel(
    private val productRepo: ProductRepository,
    private val imageRepo: ImageRepository,
    private val sellerRepo: SellerRepository
) : ViewModel() {

    var isSaving = mutableStateOf<Boolean>(value = false)

    fun createProduct(product: Product, imageUris: List<Uri>, onComplete: () -> Unit) {
        viewModelScope.launch {
            isSaving.value = true

            val index = SearchIndexer.createIndex(
                product.title,
                product.description,
                product.category,
                product.tags
            )

            val urls = imageRepo.uploadProductImages(imageUris)

            val newProduct = sellerRepo.getCurrentUserId()?.let {
                product.copy(
                    imageUrls = urls,
                    sellerId = it,
                    searchIndex = index
                )
            }

            if (newProduct != null)
                productRepo.saveProduct(newProduct)

            isSaving.value = false
            onComplete()
        }
    }

    fun updateProduct(product: Product, newImageUris: List<Uri>, onComplete: () -> Unit) {
        viewModelScope.launch {
            isSaving.value = true

            val uploadedUrls = if (newImageUris.isNotEmpty()) {
                imageRepo.uploadProductImages(newImageUris)
            } else {
                emptyList()
            }

            val finalUrls = product.imageUrls + uploadedUrls

            val index = SearchIndexer.createIndex(
                product.title,
                product.description,
                product.category,
                product.tags
            )

            val updatedProduct = product.copy(
                imageUrls = finalUrls,
                searchIndex = index
            )

            productRepo.saveProduct(updatedProduct)

            isSaving.value = false
            onComplete()
        }
    }
}