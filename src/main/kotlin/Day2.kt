import java.io.File

fun main() {
    val plays = File("inputs/day2.txt").readLines()
        .map {
            val (opponent, mine) = it.split(" ")
            opponent.first() to mine.first()
        }

    println(part1(plays))
    println(part2(plays))
}

fun part1(plays: List<Pair<Char, Char>>): Int {
    return plays.sumOf { (opponent, mine) ->
        val normalized = mine.minus(23)
        normalized.score() + opponent.play(normalized)
    }
}

fun part2(plays: List<Pair<Char, Char>>): Int {
    return plays.sumOf { (opponent, outcome) ->
        when (outcome) {
            'X' -> opponent.lose().score()
            'Y' -> opponent.draw().score() + 3
            else -> opponent.win().score() + 6
        }
    }
}

fun Char.score(): Int {
    return zeroOut() + 1
}

fun Char.win(): Char {
    return (((zeroOut() + 1) % 3) + 'A'.code).toChar()
}

fun Char.draw(): Char {
    return this
}

fun Char.lose(): Char {
    return (((zeroOut() + 2) % 3) + 'A'.code).toChar()
}

fun Char.zeroOut(): Int = minus('A')

fun Char.play(against: Char): Int {
    if (draw() == against) return 3
    if (win() == against) return 6
    return 0
}