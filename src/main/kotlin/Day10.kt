import java.io.File
import kotlin.math.absoluteValue

fun main() {
    val lines = File("inputs/day10.txt").readLines()

    val registerValues = createRegisterValues(lines)
    println(part1(registerValues))
    println(part2(registerValues))
}

private fun part1(registerValues: List<Int>): Int {
    return (20..220 step 40).sumOf { it * registerValues[it - 1] }
}

private fun part2(registerValues: List<Int>): String {
    return registerValues
        .take(240)
        .withIndex()
        .joinToString(separator = "") { (index, value) ->
            val newline = if (index > 0 && index % 40 == 0) "\n" else ""
            val pixel = if (((index % 40) - value).absoluteValue <= 1) "#" else "."
            newline + pixel
        }
}

private fun createRegisterValues(lines: List<String>) =
    lines.fold(listOf(1)) { registerValues, instruction ->
        val parts = instruction.split(" ")
        registerValues + registerValues.last() + when (parts.first()) {
            "addx" -> listOf(registerValues.last() + parts.last().toInt())
            else -> emptyList()
        }
    }
