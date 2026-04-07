package denis.and.co.handshop.utils

object SearchIndexer {
    fun createIndex(title: String, description: String, category: String, tags: List<String>): List<String> {
        val input = "$title $description $category ${tags.joinToString(" ")}".lowercase()
        val cleanText = input.filter { it.isLetterOrDigit() || it.isWhitespace() }
        val words = cleanText.split(" ").filter { it.length >= 2 }

        val trigrams = mutableSetOf<String>()
        for (word in words) {
            if (word.length == 2) {
                trigrams.add(word)
            } else {
                for (i in 0..word.length - 3) {
                    trigrams.add(word.substring(i, i + 3))
                }
            }
        }
        return trigrams.toList()
    }
}