import java.io.File
import java.util.*
import kotlin.system.measureTimeMillis

fun main() {
    val lines = File("inputs/day24.txt").readLines()
    val (blizzards, max) = readBlizzards(lines)

    val lcm = lcm(max.row + 1, max.col + 1)
    val constellations = Array(lcm) { BlizzardMap(blizzards) }

    repeat(lcm) { i ->
        constellations[i] =
            if (i == 0) BlizzardMap(blizzards) else BlizzardMap(constellations[i - 1].blizzards.update(max))
    }

    val coordinates = constellations.map { it.coordinates }

    println(measureTimeMillis { println(part1(lcm, max, coordinates)) })
    println(part2(lcm, max, coordinates))
}

fun part2(lcm: Int, max: Coordinate, constellations: List<Set<Coordinate>>): Int {
    val start = Coordinate(-1, 0)
    val goal = Coordinate(max.row + 1, max.col)
    val minToEnd = runToEnd(lcm, goal, constellations, max, BlizzardState(start, 0))
    val minBackToStart = runToEnd(lcm, start, constellations, max, BlizzardState(goal, minToEnd))
    return runToEnd(lcm, goal, constellations, max, BlizzardState(start, minBackToStart))
}

private fun part1(
    lcm: Int,
    max: Coordinate,
    constellations: List<Set<Coordinate>>
): Int {
    val start = Coordinate(-1, 0)
    val goal = Coordinate(max.row + 1, max.col)
    return runToEnd(lcm, goal, constellations, max, BlizzardState(start, 0))
}

private fun runToEnd(
    lcm: Int,
    goal: Coordinate,
    constellations: List<Set<Coordinate>>,
    max: Coordinate,
    startState: BlizzardState,
): Int {
    val queue = LinkedList<BlizzardState>().apply {
        add(startState)
    }

    var minSteps = Int.MAX_VALUE
    val seenStates = mutableMapOf<Pair<Int, Coordinate>, Int>()

    while (queue.isNotEmpty()) {
        val currentState = queue.removeFirst()

        val key = Pair(currentState.steps % lcm, currentState.position)
        val seen = seenStates[key]
        val minRemainingSteps = goal.col - currentState.position.col + (goal.row - currentState.position.row)

        if (currentState.steps + minRemainingSteps >= minSteps || (seen != null && seen <= currentState.steps)) {
            continue
        }

        seenStates[key] = currentState.steps

        if (currentState.position == goal) {
            minSteps = minSteps.coerceAtMost(currentState.steps)
            continue
        }

        val possibles = currentState.position.neighbours()
            .filter { it == goal || (it.col >= 0 && it.row >= 0 && it.row <= max.row && it.col <= max.col) } +
                currentState.position

        queue.addAll(possibles
            .filter { !constellations[(currentState.steps + 1) % constellations.size].contains(it) }
            .map { BlizzardState(it, currentState.steps + 1) })
    }

    return minSteps
}

private fun Set<Blizzard>.update(max: Coordinate): Set<Blizzard> {
    return map { blizzard ->
        val coordinate = blizzard.coordinate
        val new = when (blizzard.direction) {
            Direction.Right -> {
                blizzard.copy(coordinate = coordinate.copy(col = (coordinate.col + 1) % (max.col + 1)))
            }
            Direction.Down ->
                blizzard.copy(coordinate = coordinate.copy(row = (coordinate.row + 1) % (max.row + 1)))
            Direction.Left -> {
                var col = coordinate.col - 1
                if (col < 0) {
                    col = max.col
                }
                blizzard.copy(coordinate = coordinate.copy(col = col))
            }
            Direction.Up -> {
                var row = coordinate.row - 1
                if (row < 0) {
                    row = max.row
                }
                blizzard.copy(coordinate = coordinate.copy(row = row))
            }
        }

        new
    }.toSet()
}

private fun Coordinate.neighbours(): List<Coordinate> {
    return listOf(
        copy(row = row + 1),
        copy(row = row - 1),
        copy(col = col + 1),
        copy(col = col - 1)
    )
}

fun readBlizzards(lines: List<String>): Pair<Set<Blizzard>, Coordinate> {
    var maxRow = 0
    var maxCol = 0
    val blizzards = mutableSetOf<Blizzard>()

    for ((row, line) in lines.drop(1).dropLast(1).withIndex()) {
        for ((col, c) in line.drop(1).dropLast(1).withIndex()) {
            if (c == '#') continue

            maxRow = maxRow.coerceAtLeast(row)
            maxCol = maxCol.coerceAtLeast(col)

            when (c) {
                '>' -> blizzards.add(Blizzard(Coordinate(row, col), Direction.Right))
                '<' -> blizzards.add(Blizzard(Coordinate(row, col), Direction.Left))
                '^' -> blizzards.add(Blizzard(Coordinate(row, col), Direction.Up))
                'v' -> blizzards.add(Blizzard(Coordinate(row, col), Direction.Down))
            }
        }
    }

    return Pair(blizzards, Coordinate(maxRow, maxCol))
}

private fun lcm(a: Int, b: Int): Int {
    return a * (b / gcd(a, b))
}

private fun gcd(f: Int, s: Int): Int {
    var a = f
    var b = s
    while (b > 0) {
        val temp = b
        b = a % b
        a = temp
    }
    return a
}

data class BlizzardMap(val blizzards: Set<Blizzard>) {
    val coordinates: Set<Coordinate> = blizzards.map { it.coordinate }.toSet()
}

data class BlizzardState(val position: Coordinate, val steps: Int)

data class Blizzard(val coordinate: Coordinate, val direction: Direction)