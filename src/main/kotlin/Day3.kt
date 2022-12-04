import java.io.File

fun main() {
    val rucksacks = File("inputs/day3.txt").readLines()
    println(part1(rucksacks))
    println(part2(rucksacks))
}

private fun part1(rucksacks: List<String>): Int {
    return rucksacks.sumOf { rucksack ->
        val compartments = rucksack.compartments()
        compartments.first.intersect(compartments.second).first().priority()
    }
}

private fun part2(rucksacks: List<String>): Int {
    return rucksacks.windowed(3, 3).sumOf { group ->
        group
            .map { it.itemize() }.reduce { f, s -> f.intersect(s) }
            .first()
            .priority()
    }
}

fun String.compartments(): Pair<Set<Char>, Set<Char>> {
    val middle = length / 2
    return Pair(substring(0, middle).itemize(), substring(middle).itemize())
}

fun Char.priority(): Int = if (isLowerCase()) {
    minus('a') + 1
} else {
    minus('A') + 27
}

fun String.itemize(): Set<Char> = toCharArray().toSet()