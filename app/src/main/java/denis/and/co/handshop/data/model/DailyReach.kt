package denis.and.co.handshop.data.model

import com.google.firebase.firestore.PropertyName

data class DailyReach(
    @get:PropertyName("date")
    @set:PropertyName("date")
    var date: String = "",

    @get:PropertyName("impressions")
    @set:PropertyName("impressions")
    var impressions: Long = 0,

    @get:PropertyName("clicks")
    @set:PropertyName("clicks")
    var clicks: Long = 0,

    @get:PropertyName("addedToLiked")
    @set:PropertyName("addedToLiked")
    var addedToLiked: Map<String, Long> = emptyMap()
)