import java.io.File

fun main() {
    val pairRegex = Regex("([0-9]+)-([0-9]+),([0-9]+)-([0-9]+)")

    val segments = File("inputs/day4.txt")
        .readLines()
        .map { segment ->
            val (firstStart, firstEnd, secondStart, secondEnd) = pairRegex.find(segment)!!.destructured
            Segment(firstStart.toInt(), firstEnd.toInt(), secondStart.toInt(), secondEnd.toInt())
        }

    println(segments.count { it.fullOverlap() })
    println(segments.count { it.partialOverlap() })
}

data class Segment(val firstStart: Int, val firstEnd: Int, val secondStart: Int, val secondEnd: Int) {
    fun fullOverlap(): Boolean =
        (firstStart >= secondStart && firstEnd <= secondEnd) ||
                (secondStart >= firstStart && secondEnd <= firstEnd)

    fun partialOverlap(): Boolean {
        return firstStart in secondStart..secondEnd ||
                firstEnd in secondStart..secondEnd ||
                secondStart in firstStart..firstEnd ||
                secondEnd in firstStart..firstEnd
    }
}