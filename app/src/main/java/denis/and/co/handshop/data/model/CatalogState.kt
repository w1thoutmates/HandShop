package denis.and.co.handshop.data.model

sealed class CatalogState {
    object Loading : CatalogState()
    data class Success(val items: List<ProductWithSeller>) : CatalogState()
    data class Error(val message: String) : CatalogState()
    object Empty : CatalogState()
    object SearchEmpty : CatalogState()
}

data class ProductWithSeller(
    val product: Product,
    val seller: Seller?
)
