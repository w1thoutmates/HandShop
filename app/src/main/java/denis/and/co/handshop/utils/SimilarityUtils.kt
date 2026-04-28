package denis.and.co.handshop.utils

object SimilarityUtils {
    private fun String.toTrigrams(): Set<String> {
        return this.lowercase()
            .windowed(3)
            .toSet()
    }

    fun calculateSimilarity(str1: String, str2: String): Double {
        val set1 = str1.toTrigrams()
        val set2 = str2.toTrigrams()
        if (set1.isEmpty() && set2.isEmpty()) return 1.0
        val intersection = set1.intersect(set2).size
        val union = set1.union(set2).size
        return intersection.toDouble() / union
    }
}