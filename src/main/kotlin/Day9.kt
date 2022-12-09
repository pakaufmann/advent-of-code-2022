import java.io.File
import kotlin.math.absoluteValue

fun main() {
    val input = File("inputs/day9.txt").readLines()

    println(run(input, 2))
    println(run(input, 10))
}

private fun run(input: List<String>, knots: Int): Int {
    val startState = Rope(Array(knots) { Point(0, 0) }.toList())
    return input.fold(startState) { state, instruction ->
        val (dir, length) = instruction.split(" ")

        (1..length.toInt()).fold(state, when (dir) {
            "R" -> { s, _ -> s.move(x = 1) }
            "L" -> { s, _ -> s.move(x = -1) }
            "D" -> { s, _ -> s.move(y = 1) }
            "U" -> { s, _ -> s.move(y = -1) }
            else -> throw Exception("wrong direction")
        })
    }.visited.size
}

data class Rope(val points: List<Point>, val visited: Set<Point> = emptySet()) {
    fun move(x: Int = 0, y: Int = 0): Rope {
        var current = points.first().run {
            copy(x = this.x + x, y = this.y + y)
        }
        val updatedPoints = mutableListOf(current)

        for (point in points.drop(1)) {
            current = if (point.touches(current)) point else point.moveIntoTouchTo(current)
            updatedPoints.add(current)
        }

        return Rope(
            updatedPoints,
            visited + updatedPoints.last()
        )
    }
}

data class Point(val x: Int, val y: Int) {
    fun touches(point: Point): Boolean {
        return (x - point.x).absoluteValue <= 1 && (y - point.y).absoluteValue <= 1
    }

    fun moveIntoTouchTo(point: Point): Point {
        if (point.x == x || point.y == y) {
            Point(x - 1, y).apply { if (touches(point)) return this }
            Point(x + 1, y).apply { if (touches(point)) return this }
            Point(x, y - 1).apply { if (touches(point)) return this }
            return Point(x, y + 1)
        }

        Point(x - 1, y - 1).apply { if (touches(point)) return this }
        Point(x + 1, y + 1).apply { if (touches(point)) return this }
        Point(x - 1, y + 1).apply { if (touches(point)) return this }
        return Point(x + 1, y - 1)
    }
}