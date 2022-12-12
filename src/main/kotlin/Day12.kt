import java.io.File
import java.util.*
import kotlin.streams.toList

fun main() {
    val lines = File("inputs/day12.txt").readLines()
    val square = lines.map { line ->
        line.chars().map {
            when (it) {
                'S'.code -> 0
                'E'.code -> 26
                else -> it - 'a'.code
            }
        }.toList()
    }

    println(part1(lines, square))
    println(part2(lines, square))
}

private fun part1(
    lines: List<String>,
    square: List<List<Int>>
): Int {
    val start = lines.findCoordinate('S') ?: throw Exception("no start found")
    val end = lines.findCoordinate('E') ?: throw Exception("no end found")

    return findPath(start, end, square)
}

private fun part2(
    lines: List<String>,
    square: List<List<Int>>
): Int? {
    val end = lines.findCoordinate('E')!!

    return lines.findCoordinates('a')
        .map { findPath(it, end, square) }
        .filter { it != -1 }
        .minOrNull()
}

private fun findPath(
    start: Coordinate,
    end: Coordinate,
    square: List<List<Int>>
): Int {
    val searches = PriorityQueue<Candidate>(Comparator.comparing { it.length })
    searches.add(Candidate(start, 0))

    val found = mutableSetOf<Coordinate>()

    while (searches.isNotEmpty()) {
        val (coordinate, length) = searches.poll()

        if (found.contains(coordinate)) continue
        found.add(coordinate)

        val neighbours = square.getValidNeighbours(coordinate)
            .map { Candidate(it, length + 1) }

        if (neighbours.any { it.coordinate == end }) return length + 1

        searches.addAll(neighbours)
    }

    return -1
}

private fun List<List<Int>>.getValidNeighbours(coordinate: Coordinate): List<Coordinate> {
    return listOf(
        coordinate.copy(row = coordinate.row - 1),
        coordinate.copy(row = coordinate.row + 1),
        coordinate.copy(col = coordinate.col - 1),
        coordinate.copy(col = coordinate.col + 1)
    ).filterNot {
        it.row < 0 ||
                it.col < 0 ||
                it.row >= this.size ||
                it.col >= this[0].size ||
                this[it.row][it.col] > (this[coordinate.row][coordinate.col] + 1)
    }
}

fun List<String>.findCoordinate(char: Char): Coordinate? {
    for ((row, l) in withIndex()) {
        for ((col, c) in l.withIndex()) {
            if (c == char) return Coordinate(row, col)
        }
    }
    return null
}

private fun List<String>.findCoordinates(char: Char): List<Coordinate> {
    return withIndex().flatMap { (row, l) ->
        l.withIndex().filter { (_, c) -> c == char }.map { (col, _) -> Coordinate(row, col) }
    }
}

data class Coordinate(val row: Int, val col: Int)

data class Candidate(val coordinate: Coordinate, val length: Int)