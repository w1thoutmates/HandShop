package denis.and.co.handshop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.toRoute
import denis.and.co.handshop.data.model.Product
import denis.and.co.handshop.di.AppDependencies
import denis.and.co.handshop.ui.navigation.CreateProductRoute
import denis.and.co.handshop.ui.navigation.CreateProfileRoute
import denis.and.co.handshop.ui.navigation.EditProductRoute
import denis.and.co.handshop.ui.navigation.EditProfileRoute
import denis.and.co.handshop.ui.navigation.LikedRoute
import denis.and.co.handshop.ui.navigation.LoginRoute
import denis.and.co.handshop.ui.navigation.ProductDetailsRoute
import denis.and.co.handshop.ui.navigation.ProfileRoute
import denis.and.co.handshop.ui.navigation.RecommendationRoute
import denis.and.co.handshop.ui.navigation.ReviewsRoute
import denis.and.co.handshop.ui.navigation.SearchByCategoryRoute
import denis.and.co.handshop.ui.screens.EditProfileScreen
import denis.and.co.handshop.ui.screens.LikedProductsScreen
import denis.and.co.handshop.ui.screens.LoginScreen
import denis.and.co.handshop.ui.screens.ProductCreatingScreen
import denis.and.co.handshop.ui.screens.ProductDetailsScreen
import denis.and.co.handshop.ui.screens.RecommendationScreen
import denis.and.co.handshop.ui.screens.ReviewsScreen
import denis.and.co.handshop.ui.screens.SearchingScreen
import denis.and.co.handshop.ui.screens.SellerProfileScreen
import denis.and.co.handshop.ui.theme.Accent
import denis.and.co.handshop.viewmodel.AuthViewModel
import denis.and.co.handshop.viewmodel.CatalogViewModel
import denis.and.co.handshop.viewmodel.CreateProductViewModel
import denis.and.co.handshop.viewmodel.EditProfileViewModel
import denis.and.co.handshop.viewmodel.LikedViewModel
import denis.and.co.handshop.viewmodel.ProductDetailsVM
import denis.and.co.handshop.viewmodel.ProfileViewModel
import denis.and.co.handshop.viewmodel.ReviewsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppDependencies.init(applicationContext)

        enableEdgeToEdge()
        setContent {
            val authViewModel: AuthViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return AuthViewModel(AppDependencies.sellerRepository) as T
                    }
                }
            )
            val startDestination = authViewModel.startDestination

            if (startDestination == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Accent)
                }
                return@setContent
            }

            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = startDestination
            ) {

                composable<LoginRoute> {
                    LoginScreen(onAuthSuccess = { authViewModel.checkAuthState() })
                }

                composable<CreateProfileRoute> {
                    val editProfileVM: EditProfileViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return EditProfileViewModel(
                                    AppDependencies.imageRepository,
                                    AppDependencies.sellerRepository
                                ) as T
                            }
                        }
                    )

                    EditProfileScreen(
                        navController = navController,
                        viewModel = editProfileVM,
                        isFirstCreation = true
                    )
                }

                composable<RecommendationRoute> {
                    val catalogViewModel: CatalogViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return CatalogViewModel(
                                    AppDependencies.productRepository,
                                    AppDependencies.sellerRepository
                                ) as T
                            }
                        }
                    )

                    RecommendationScreen(
                        navController = navController,
                        catalogViewModel = catalogViewModel
                    )
                }

                composable<ProductDetailsRoute> { backStackEntry ->
                    val route: ProductDetailsRoute = backStackEntry.toRoute()

                    val detailsVm: ProductDetailsVM = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return ProductDetailsVM(
                                    AppDependencies.productRepository,
                                    AppDependencies.sellerRepository
                                ) as T
                            }
                        }
                    )

                    val product by detailsVm.product.collectAsState()

                    val likedViewModel: LikedViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return LikedViewModel(
                                    AppDependencies.productRepository,
                                    AppDependencies.sellerRepository
                                ) as T
                            }
                        }
                    )

                    LaunchedEffect(route.productId) {
                        detailsVm.loadProduct(route.productId)
                    }

                    product?.let { currentProduct ->
                        ProductDetailsScreen(
                            product = currentProduct,
                            viewModel = detailsVm,
                            navController = navController,
                            likedViewModel = likedViewModel
                        )
                    } ?: run {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Accent)
                        }
                    }
                }

                composable<CreateProductRoute> { backStackEntry ->
                    val createProductVM: CreateProductViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return CreateProductViewModel(
                                    AppDependencies.productRepository,
                                    AppDependencies.imageRepository,
                                    AppDependencies.sellerRepository
                                ) as T
                            }
                        }
                    )

                    ProductCreatingScreen(
                        navController = navController,
                        viewModel = createProductVM
                    )
                }

                composable<ProfileRoute> { backStackEntry ->
                    val route: ProfileRoute = backStackEntry.toRoute()

                    val profileVm: ProfileViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T: ViewModel> create(modelClass: Class<T>): T {
                                return ProfileViewModel(
                                    sellerRepo = AppDependencies.sellerRepository,
                                    productRepo = AppDependencies.productRepository
                                ) as T
                            }
                        }
                    )

                    SellerProfileScreen(
                        navController = navController,
                        sellerId = route.sellerId,
                        viewModel = profileVm
                    )
                }
                composable<LikedRoute> {
                    val likedViewModel: LikedViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return LikedViewModel(
                                    AppDependencies.productRepository,
                                    AppDependencies.sellerRepository
                                ) as T
                            }
                        }
                    )
                    LikedProductsScreen(
                        navController = navController,
                        viewModel = likedViewModel
                    )
                }

                composable<SearchByCategoryRoute> {
                    val catalogViewModel: CatalogViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return CatalogViewModel(
                                AppDependencies.productRepository,
                                AppDependencies.sellerRepository
                            ) as T
                        }
                    }
                )
                    SearchingScreen(navController, catalogViewModel)
                }

                composable<EditProfileRoute> { backStackEntry ->
                    val currentUid = authViewModel.getAuth().currentUser?.uid

                    val editProfileVM: EditProfileViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return EditProfileViewModel(
                                    AppDependencies.imageRepository,
                                    AppDependencies.sellerRepository
                                ) as T
                            }
                        }
                    )

                    LaunchedEffect(currentUid) {
                        currentUid?.let { editProfileVM.loadProfile(it) }
                    }

                    EditProfileScreen(
                        navController = navController,
                        viewModel = editProfileVM,
                        isFirstCreation = false
                    )
                }

                composable<EditProductRoute> { backStackEntry ->
                    val route: EditProductRoute = backStackEntry.toRoute()

                    val createProductVM: CreateProductViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return CreateProductViewModel(
                                    AppDependencies.productRepository,
                                    AppDependencies.imageRepository,
                                    AppDependencies.sellerRepository
                                ) as T
                            }
                        }
                    )

                    var productToEdit by remember { mutableStateOf<Product?>(null) }
                    var isLoading by remember { mutableStateOf(true) }

                    LaunchedEffect(route.productId) {
                        val result = AppDependencies.productRepository.getProductById(route.productId)
                        productToEdit = result.getOrNull()
                        isLoading = false
                    }

                    if (isLoading) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Accent)
                        }
                    } else {
                        ProductCreatingScreen(
                            navController = navController,
                            viewModel = createProductVM,
                            initialProduct = productToEdit
                        )
                    }
                }

                composable<ReviewsRoute> { backStackEntry ->
                    val route: ReviewsRoute = backStackEntry.toRoute()

                    val reviewsVm: ReviewsViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return ReviewsViewModel(
                                    sellerId = route.sellerId,
                                    reviewsRepo = AppDependencies.reviewsRepository,
                                    sellerRepo = AppDependencies.sellerRepository
                                ) as T
                            }
                        }
                    )

                    ReviewsScreen(viewModel = reviewsVm, navController = navController)
                }
            }
        }
    }
}