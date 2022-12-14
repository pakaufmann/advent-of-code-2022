import java.io.File

fun main() {
    val rocks = File("inputs/day14.txt").readLines()

    println(part1(readObstacles(rocks)))
    println(part2(readObstacles(rocks)))
}

private val start = Coordinate(0, 500)

private fun part1(obstacles: MutableSet<Coordinate>): Int {
    val lastObstacle = obstacles.maxOf { it.row }
    return generateSequence { obstacles.addSand(lastObstacle) }
        .takeWhile { (_, nextPosition) -> nextPosition == null }
        .count()
}

private fun part2(obstacles: MutableSet<Coordinate>): Int {
    val lastObstacle = obstacles.maxOf { it.row } + 1
    return generateSequence { obstacles.addSand(lastObstacle) }
        .takeWhile { (endPosition, _) -> endPosition != start }
        .count() + 1
}

private fun readObstacles(obstacles: List<String>) = obstacles.flatMap { obstacle ->
    val points = obstacle.split(" -> ")
    val result = mutableListOf<Coordinate>()
    var lastCoordinate: Coordinate = points.first().toCoordinate()

    for (segment in points.drop(1)) {
        val coordinate = segment.toCoordinate()

        val (minCol, maxCol) = listOf(lastCoordinate.col, coordinate.col).sorted()
        val (minRow, maxRow) = listOf(lastCoordinate.row, coordinate.row).sorted()

        result += (minCol..maxCol).flatMap { col -> (minRow..maxRow).map { row -> Coordinate(row, col) } }
        lastCoordinate = coordinate
    }

    result
}.toMutableSet()

fun MutableSet<Coordinate>.addSand(floor: Int): State =
    generateSequence(State(start)) { (_, current) ->
        State(current!!, current.possibleNext().firstOrNull { !contains(it) })
    }
        .first { it.next == null || it.next.row > floor }
        .apply { add(current) }

data class State(val current: Coordinate, val next: Coordinate? = current)

private fun Coordinate.possibleNext(): List<Coordinate> {
    return listOf(
        Coordinate(this.row + 1, this.col),
        Coordinate(this.row + 1, this.col - 1),
        Coordinate(this.row + 1, this.col + 1),
    )
}

private fun String.toCoordinate(): Coordinate {
    val (col, row) = split(",")
    return Coordinate(row.toInt(), col.toInt())
}
