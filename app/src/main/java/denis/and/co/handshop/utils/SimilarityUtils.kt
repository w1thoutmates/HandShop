package denis.and.co.handshop.utils

object SimilarityUtils {
    private fun String.toTrigrams(): Set<String> {
        return this.lowercase()
            .windowed(3)
            .toSet()
    }

    fun calculateSimilarityOld(str1: String, str2: String): Double {
        val set1 = str1.toTrigrams()
        val set2 = str2.toTrigrams()
        if (set1.isEmpty() && set2.isEmpty()) return 1.0
        val intersection = set1.intersect(set2).size
        val union = set1.union(set2).size
        return intersection.toDouble() / union
    }

    fun calculateSimilarity(str1: String, str2: String, tags1: List<String>, tags2: List<String>): Double {
        val words1 = str1.lowercase().split(" ").filter { it.length > 2 }.toSet()
        val words2 = str2.lowercase().split(" ").filter { it.length > 2 }.toSet()

        val wordOverlap = if (words1.isEmpty() || words2.isEmpty()) 0.0 else {
            val intersection = words1.intersect(words2).size.toDouble()
            intersection / minOf(words1.size, words2.size)
        }

        val t1 = tags1.map { it.lowercase() }.toSet()
        val t2 = tags2.map { it.lowercase() }.toSet()

        val tagOverlap = if (t1.isEmpty() || t2.isEmpty()) 0.0 else {
            val intersection = t1.intersect(t2).size.toDouble()
            intersection / minOf(t1.size, t2.size)
        }

        return maxOf(wordOverlap, tagOverlap)
    }
}