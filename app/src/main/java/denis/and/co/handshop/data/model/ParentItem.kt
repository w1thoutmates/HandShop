package denis.and.co.handshop.data.model

data class ParentItem(
    val id: String,
    val title: String,
    val children: List<ChildItem>
)