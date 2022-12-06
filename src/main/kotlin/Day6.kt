import java.io.File

fun main() {
    val line = File("inputs/day6.txt").readText()

    println(findDistinct(line, 4))
    println(findDistinct(line, 14))
}

private fun findDistinct(line: String, length: Int): Int {
    return length + line
        .windowed(length)
        .indexOfFirst { it.toSet().size == length }
}