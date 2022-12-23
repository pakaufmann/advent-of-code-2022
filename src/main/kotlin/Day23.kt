import java.io.File

fun main() {
    val elves = readElves(File("inputs/day23.txt").readLines())
    println(part1(elves))
    println(part2(elves))
}

private fun part1(elves: Set<Elf>): Int {
    return generateSequence(elves) { it.runRound() }
        .drop(10)
        .first()
        .countBoard()
}

private fun part2(elves: Set<Elf>): Int {
    return generateSequence(elves) { it.runRound() }
        .windowed(2)
        .takeWhile {
            it.first().map { it.coordinate } != it[1].map { it.coordinate }
        }
        .count() + 1
}

fun Set<Elf>.countBoard(): Int {
    val rows = maxOf { it.coordinate.row } - minOf { it.coordinate.row } + 1
    val cols = maxOf { it.coordinate.col } - minOf { it.coordinate.col } + 1
    return rows * cols - this.size
}

private fun Set<Elf>.runRound(): Set<Elf> {
    val coordinates = map { it.coordinate }.toSet()
    val propositions = mutableMapOf<Coordinate, List<Elf>>()

    for (elf in this) {
        val proposition = elf.propose(coordinates)
        propositions.compute(proposition) { _, s ->
            if (s == null) listOf(elf) else s + elf
        }
    }

    return propositions.flatMap { (coordinate, elves) ->
        elves.map {
            if (elves.size == 1) {
                Elf(coordinate, it.considerStack.drop(1) + it.considerStack.first())
            } else {
                it.copy(considerStack = it.considerStack.drop(1) + it.considerStack.first())
            }
        }
    }.toSet()
}

fun readElves(lines: List<String>): Set<Elf> =
    lines.withIndex()
        .flatMap { (row, line) ->
            line.withIndex().mapNotNull { (col, c) ->
                if (c == '#') {
                    Elf(Coordinate(row, col), ElfDirection.values().toList())
                } else {
                    null
                }
            }
        }
        .toSet()

enum class ElfDirection {
    NORTH,
    SOUTH,
    WEST,
    EAST
}

data class Elf(val coordinate: Coordinate, val considerStack: List<ElfDirection>) {
    fun propose(map: Set<Coordinate>): Coordinate {
        val neighbours = listOf(
            coordinate.copy(row = coordinate.row - 1),
            coordinate.copy(row = coordinate.row - 1, col = coordinate.col - 1),
            coordinate.copy(row = coordinate.row - 1, col = coordinate.col + 1),
            coordinate.copy(col = coordinate.col - 1),
            coordinate.copy(col = coordinate.col + 1),
            coordinate.copy(row = coordinate.row + 1, col = coordinate.col - 1),
            coordinate.copy(row = coordinate.row + 1),
            coordinate.copy(row = coordinate.row + 1, col = coordinate.col + 1)
        )

        if (neighbours.all { !map.contains(it) }) {
            return coordinate
        }

        for (proposition in considerStack) {
            val (toCheck, dRow, dCol) = when (proposition) {
                ElfDirection.NORTH -> Triple(neighbours.filter { it.row == coordinate.row - 1 }, -1, 0)
                ElfDirection.SOUTH -> Triple(neighbours.filter { it.row == coordinate.row + 1 }, 1, 0)
                ElfDirection.WEST -> Triple(neighbours.filter { it.col == coordinate.col - 1 }, 0, -1)
                ElfDirection.EAST -> Triple(neighbours.filter { it.col == coordinate.col + 1 }, 0, 1)
            }

            if (toCheck.all { !map.contains(it) }) {
                return Coordinate(coordinate.row + dRow, coordinate.col + dCol)
            }
        }

        return coordinate
    }
}