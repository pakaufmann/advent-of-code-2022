import java.io.File

fun main() {
    val totals = countCalories()
    println(totals.first())
    println(totals.take(3).sum())
}

fun countCalories(): List<Int> {
    val lines = File("inputs/day1.txt").readLines()

    return (lines + "").fold(Pair(listOf<Int>(), 0)) { (max, cur), line ->
        if (line == "") {
            Pair(max + cur, 0)
        } else {
            Pair(max, cur + line.toInt())
        }
    }.first.sortedDescending()
}