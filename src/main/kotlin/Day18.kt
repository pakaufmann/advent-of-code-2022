import java.io.File

fun main() {
    val cubeRegex = Regex("([0-9]+),([0-9]+),([0-9]+)")

    val cubes = File("inputs/day18.txt").readLines().map { line ->
        val (x, y, z) = cubeRegex.find(line)!!.destructured
        Cube(x.toInt(), y.toInt(), z.toInt())
    }.toSet()

    println(part1(cubes))
    println(part2(cubes))
}

private fun part1(cubes: Set<Cube>): Int {
    return cubes.sumOf { cube -> cube.neighbours().count { !cubes.contains(it) } }
}

private fun part2(cubes: Set<Cube>): Int {
    val topLeft = cubes.topLeft()
    val bottomRight = cubes.bottomRight()

    val queue = ArrayDeque<Cube>()
    queue.add(topLeft)

    val visited = mutableSetOf<Cube>()

    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()

        if (!visited.add(current)) {
            continue
        }

        val validNeighbours = current.neighbours().filter {
            it.x >= topLeft.x && it.y >= topLeft.y && it.z >= topLeft.z &&
                    it.x <= bottomRight.x && it.y <= bottomRight.y && it.z <= bottomRight.z &&
                    !cubes.contains(it)
        }

        queue.addAll(validNeighbours)
    }

    return cubes.sumOf { cube -> cube.neighbours().count { visited.contains(it) } }
}

private fun Set<Cube>.topLeft(): Cube {
    return Cube(minOf { it.x } - 1, minOf { it.y } - 1, minOf { it.z } - 1)
}

private fun Set<Cube>.bottomRight(): Cube {
    return Cube(maxOf { it.x } + 1, maxOf { it.y } + 1, maxOf { it.z } + 1)
}

data class Cube(val x: Int, val y: Int, val z: Int) {
    fun neighbours(): List<Cube> = listOf(
        copy(x = x - 1),
        copy(x = x + 1),
        copy(y = y - 1),
        copy(y = y + 1),
        copy(z = z - 1),
        copy(z = z + 1)
    )
}