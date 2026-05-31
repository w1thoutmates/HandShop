package denis.and.co.handshop.utils

object SearchIndexer {
    fun createIndex(title: String, description: String, category: String, tags: List<String>): List<String> {
        val input = "$title $description $category ${tags.joinToString(" ")}".lowercase()
        val cleanText = input.filter { it.isLetterOrDigit() || it.isWhitespace() }
        val words = cleanText.split("\\s+".toRegex()).filter { it.length >= 2 }

        val ngrams = mutableSetOf<String>()
        for (word in words) {
            for (i in 0..word.length - 2) {
                ngrams.add(word.substring(i, i + 2))
            }
            if (word.length >= 3) {
                for (i in 0..word.length - 3) {
                    ngrams.add(word.substring(i, i + 3))
                }
            }
        }
        return ngrams.toList()
    }
}