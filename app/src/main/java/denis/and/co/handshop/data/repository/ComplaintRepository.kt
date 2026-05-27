package denis.and.co.handshop.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import denis.and.co.handshop.data.enums.ComplaintStatus
import denis.and.co.handshop.data.model.Complaint
import kotlinx.coroutines.tasks.await

class ComplaintRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val complaintsCollection = firestore.collection("complaints")
    private val sellersCollection = firestore.collection("sellers")
    private val productsCollection = firestore.collection("products")

    suspend fun sendComplaint(complaint: Complaint): Result<Unit> {
        return try {
            val docRef = complaintsCollection.document()
            val data = mapOf(
                "id" to docRef.id,
                "reporterSellerId" to complaint.reporterSellerId,
                "reporterName" to complaint.reporterName,
                "reporterAvatar" to complaint.reporterAvatar,
                "targetSellerId" to complaint.targetSellerId,
                "targetSellerName" to complaint.targetSellerName,
                "targetSellerAvatar" to complaint.targetSellerAvatar,
                "text" to complaint.text,
                "date" to complaint.date,
                "status" to ComplaintStatus.PENDING.name
            )
            docRef.set(data).await()
            Result.success(Unit)
        } catch (ex: Exception) {
            Log.e("COMPLAINT_REPO", "Ошибка отправки жалобы", ex)
            Result.failure(ex)
        }
    }

    suspend fun getPendingComplaints(): List<Complaint> {
        return try {
            val snapshot = complaintsCollection
                .whereEqualTo("status", ComplaintStatus.PENDING.name)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                parseComplaint(doc.id, doc.data)
            }
        } catch (ex: Exception) {
            Log.e("COMPLAINT_REPO", "Ошибка загрузки жалоб: ${ex.message}", ex)
            emptyList()
        }
    }

    suspend fun warnSeller(targetSellerId: String, complaintId: String): Result<Int> {
        return try {
            var newWarningCount = 0
            firestore.runTransaction { transaction ->
                val sellerRef = sellersCollection.document(targetSellerId)
                val complaintRef = complaintsCollection.document(complaintId)

                val sellerSnap = transaction.get(sellerRef)
                val currentWarnings = when (val w = sellerSnap.get("warningsCount")) {
                    is Long -> w.toInt()
                    is Int -> w
                    else -> 0
                }
                newWarningCount = currentWarnings + 1

                transaction.update(sellerRef, "warningsCount", newWarningCount)
                transaction.update(complaintRef, "status", ComplaintStatus.RESOLVED.name)
            }.await()
            Result.success(newWarningCount)
        } catch (ex: Exception) {
            Log.e("COMPLAINT_REPO", "Ошибка выдачи предупреждения", ex)
            Result.failure(ex)
        }
    }

    suspend fun banSeller(targetSellerId: String): Result<Unit> {
        return try {
            val productsSnapshot = productsCollection
                .whereEqualTo("sellerId", targetSellerId)
                .get()
                .await()

            val batch = firestore.batch()
            productsSnapshot.documents.forEach { doc ->
                batch.delete(doc.reference)
            }
            val sellerRef = sellersCollection.document(targetSellerId)
            batch.update(sellerRef, "isBanned", true)
            batch.commit().await()

            val pendingComplaints = complaintsCollection
                .whereEqualTo("targetSellerId", targetSellerId)
                .whereEqualTo("status", ComplaintStatus.PENDING.name)
                .get()
                .await()

            if (pendingComplaints.documents.isNotEmpty()) {
                val updateBatch = firestore.batch()
                pendingComplaints.documents.forEach { doc ->
                    updateBatch.update(doc.reference, "status", ComplaintStatus.RESOLVED.name)
                }
                updateBatch.commit().await()
            }

            Result.success(Unit)
        } catch (ex: Exception) {
            Log.e("COMPLAINT_REPO", "Ошибка бана продавца", ex)
            Result.failure(ex)
        }
    }

    suspend fun rejectComplaint(complaintId: String): Result<Unit> {
        return try {
            complaintsCollection.document(complaintId)
                .update("status", ComplaintStatus.REJECTED.name)
                .await()
            Result.success(Unit)
        } catch (ex: Exception) {
            Log.e("COMPLAINT_REPO", "Ошибка отклонения жалобы", ex)
            Result.failure(ex)
        }
    }

    suspend fun ignoreReporter(reporterSellerId: String, complaintId: String): Result<Unit> {
        return try {
            complaintsCollection.document(complaintId)
                .update("status", ComplaintStatus.IGNORED.name)
                .await()

            val reporterComplaints = complaintsCollection
                .whereEqualTo("reporterSellerId", reporterSellerId)
                .whereEqualTo("status", ComplaintStatus.PENDING.name)
                .get()
                .await()

            if (reporterComplaints.documents.isNotEmpty()) {
                val batch = firestore.batch()
                reporterComplaints.documents.forEach { doc ->
                    batch.update(doc.reference, "status", ComplaintStatus.IGNORED.name)
                }
                batch.commit().await()
            }

            Result.success(Unit)
        } catch (ex: Exception) {
            Log.e("COMPLAINT_REPO", "Ошибка игнорирования отправителя", ex)
            Result.failure(ex)
        }
    }

    suspend fun clearResolvedComplaints(): Result<Unit> {
        return try {
            val statuses = listOf(
                ComplaintStatus.RESOLVED.name,
                ComplaintStatus.REJECTED.name,
                ComplaintStatus.IGNORED.name
            )
            val snapshot = complaintsCollection
                .whereIn("status", statuses)
                .get()
                .await()

            if (snapshot.documents.isNotEmpty()) {
                val batch = firestore.batch()
                snapshot.documents.forEach { doc ->
                    batch.delete(doc.reference)
                }
                batch.commit().await()
            }

            Result.success(Unit)
        } catch (ex: Exception) {
            Log.e("COMPLAINT_REPO", "Ошибка очистки жалоб", ex)
            Result.failure(ex)
        }
    }

    suspend fun isModerator(userId: String): Boolean {
        return try {
            val doc = sellersCollection.document(userId).get().await()
            doc.getString("role") == "moderator"
        } catch (ex: Exception) {
            false
        }
    }

    suspend fun getWarningsCount(sellerId: String): Int {
        return try {
            val doc = sellersCollection.document(sellerId).get().await()
            when (val w = doc.get("warningsCount")) {
                is Long -> w.toInt()
                is Int -> w
                else -> 0
            }
        } catch (ex: Exception) {
            0
        }
    }

    private fun parseComplaint(id: String, data: Map<String, Any?>?): Complaint? {
        if (data == null) return null
        return try {
            val statusStr = data["status"] as? String ?: ComplaintStatus.PENDING.name
            val status = try {
                ComplaintStatus.valueOf(statusStr)
            } catch (e: IllegalArgumentException) {
                ComplaintStatus.PENDING
            }

            Complaint(
                id = id,
                reporterSellerId = data["reporterSellerId"] as? String ?: "",
                reporterName = data["reporterName"] as? String ?: "",
                reporterAvatar = data["reporterAvatar"] as? String ?: "",
                targetSellerId = data["targetSellerId"] as? String ?: "",
                targetSellerName = data["targetSellerName"] as? String ?: "",
                targetSellerAvatar = data["targetSellerAvatar"] as? String ?: "",
                text = data["text"] as? String ?: "",
                date = when (val d = data["date"]) {
                    is Long -> d
                    is Double -> d.toLong()
                    else -> System.currentTimeMillis()
                },
                status = status
            )
        } catch (ex: Exception) {
            Log.e("COMPLAINT_REPO", "Ошибка парсинга жалобы $id", ex)
            null
        }
    }
}