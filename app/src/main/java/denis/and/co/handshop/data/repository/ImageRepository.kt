package denis.and.co.handshop.data.repository

import android.content.Context
import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class ImageRepository(private val context: Context) {

    init {
        try {
            val config = mapOf(
                "cloud_name" to "djwpsxmpe",
                "api_key" to "343896696952399",
                "api_secret" to "M58GHKyU0M3Qy5SIqD37ieZndX8" // убрать
            )
            MediaManager.init(context, config)
        } catch (e: Exception) {
            // Уже инициализирован
        }
    }

    suspend fun uploadImage(uri: Uri): String? = suspendCancellableCoroutine { continuation ->
        MediaManager.get().upload(uri)
            .unsigned("unsignedpreset")
            .callback(object : UploadCallback {
                override fun onStart(requestId: String) {}
                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}

                override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                    val url = resultData["secure_url"] as? String
                    continuation.resume(url)
                }

                override fun onError(requestId: String, error: ErrorInfo) {
                    continuation.resume(null)
                }

                override fun onReschedule(requestId: String, error: ErrorInfo) {}
            }).dispatch()
    }

    suspend fun uploadProductImages(uris: List<Uri>): List<String> = coroutineScope {
        uris.map { uri ->
            async { uploadImage(uri) }
        }.awaitAll().filterNotNull()
    }
}