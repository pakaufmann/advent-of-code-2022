import java.io.File
import java.math.BigInteger
import kotlin.math.absoluteValue

fun main() {
    val lines = File("inputs/day25.txt").readLines()
    val finalNumber = lines.sumOf { it.toNormalNumber() }
    println(finalNumber.toSnafuNumber())
}

private fun Long.toSnafuNumber(): String {
    var start = 0
    while (true) {
        val remaining = this / pow(5, start).toDouble()
        if (remaining < 2.0) {
            if (remaining < 0.5) start--
            break
        }
        start++
    }

    return (start downTo 0).fold(Pair("", this)) { (num, remaining), index ->
        val max = remaining / pow(5, index)
        val other = if (remaining < 0) -1 else 1

        val lower = remaining - (max * pow(5, index))
        val higher = remaining - ((max + other) * pow(5, index))

        val (add, newRemaining) = if (lower.absoluteValue < higher.absoluteValue) {
            Pair(max, lower)
        } else {
            Pair(max + other, higher)
        }

        val newNum = num + when (add) {
            -2L -> "="
            -1L -> "-"
            else -> add
        }

        Pair(newNum, newRemaining)
    }.first
}

private fun String.toNormalNumber(): Long {
    return reversed().withIndex().fold(0L) { num, (i, char) ->
        num + pow(5, i) * when (char) {
            '-' -> -1
            '=' -> -2
            else -> char.digitToInt()
        }
    }
}

fun pow(n: Long, exp: Int): Long {
    return BigInteger.valueOf(n).pow(exp).toLong()
}