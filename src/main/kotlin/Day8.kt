import java.io.File

fun main() {
    val lines = File("inputs/day8.txt").readLines()
    val trees = lines.map { line -> line.map { it.digitToInt() } }
    println(part1(trees))
    println(part2(trees))
}

private fun part1(heights: List<List<Int>>): Int {
    return heights.withIndex().sumOf { (row, line) ->
        line.withIndex().count { (col, _) ->
            heights.canSeeEdgeFrom(row, col)
        }
    }
}

private fun part2(heights: List<List<Int>>): Int {
    return heights.withIndex().maxOf { (row, line) ->
        line.withIndex().maxOf { (col, _) ->
            heights.scoreFrom(row, col)
        }
    }
}

private fun List<List<Int>>.canSeeEdgeFrom(row: Int, col: Int) =
    count(row - 1, col, this[row][col], rowD = -1) >= row ||
            count(row + 1, col, this[row][col], rowD = 1) >= (this.size - row - 1) ||
            count(row, col - 1, this[row][col], colD = -1) >= col ||
            count(row, col + 1, this[row][col], colD = 1) >= (this[row].size - col - 1)

private fun List<List<Int>>.scoreFrom(
    row: Int,
    col: Int,
) = count(row, col - 1, this[row][col], colD = -1, countStart = 1) *
        count(row, col + 1, this[row][col], colD = 1, countStart = 1) *
        count(row - 1, col, this[row][col], rowD = -1, countStart = 1) *
        count(row + 1, col, this[row][col], rowD = 1, countStart = 1)

fun List<List<Int>>.count(
    row: Int,
    col: Int,
    height: Int,
    rowD: Int = 0,
    colD: Int = 0,
    countStart: Int = 0
): Int {
    if (row < 0 || row >= size) return 0
    if (col < 0 || col >= this[0].size) return 0

    return if (this[row][col] < height) {
        count(
            row + rowD,
            col + colD,
            height,
            rowD,
            colD,
            countStart
        ) + 1
    } else {
        countStart
    }
}
