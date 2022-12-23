import java.io.File

fun main() {
    val input = File("inputs/day22.txt").readLines()

    val (map, instructions) = readInput(input)

    println(part1(map, instructions))
    println(part2(map, instructions))
}

fun part1(map: Map<Coordinate, Tile>, instructions: List<Instruction>): Int {
    val topRow = map.minOf { it.key.row }
    val start = map
        .filter { it.key.row == topRow && it.value.type == TileType.Free }
        .minByOrNull { it.key.col }

    var pos = start!!.key
    var direction = Direction.Right

    for (instruction in instructions) {
        when (instruction) {
            Turn.Left -> {
                direction = Direction.values()[(direction.ordinal - 1 + 4) % 4]
            }
            Turn.Right -> {
                direction = Direction.values()[(direction.ordinal + 1) % 4]
            }
            is Move -> {
                for (move in 0 until instruction.step) {
                    val newPos = when (direction) {
                        Direction.Right -> map.nextPosition(pos, dCol = 1)
                        Direction.Down -> map.nextPosition(pos, dRow = 1)
                        Direction.Left -> map.nextPosition(pos, dCol = -1)
                        Direction.Up -> map.nextPosition(pos, dRow = -1)
                    }
                    if (map[newPos]!!.type == TileType.Wall) {
                        break
                    } else {
                        pos = newPos
                    }
                }
            }
        }
    }

    return (pos.row + 1) * 1000 + (pos.col + 1) * 4 + direction.ordinal
}

fun from(
    rowFrom: List<Int>,
    colFrom: List<Int>,
    rowTo: List<Int>,
    colTo: List<Int>,
    newDir: Direction,
    fromDir: Direction
): Map<Pair<Coordinate, Direction>, Pair<Coordinate, Direction>> {
    val result = mutableMapOf<Pair<Coordinate, Direction>, Pair<Coordinate, Direction>>()
    var i = 0

    for (x in rowFrom) {
        for (y in colFrom) {
            result[Pair(Coordinate(x, y), fromDir)] = Pair(Coordinate(rowTo[i], colTo[i]), newDir)
            i++
        }
    }

    return result
}

fun part2(map: Map<Coordinate, Tile>, instructions: List<Instruction>): Int {
    val topRow = map.minOf { it.key.row }
    val start = map
        .filter { it.key.row == topRow && it.value.type == TileType.Free }
        .minByOrNull { it.key.col }

    var pos = start!!.key
    var direction = Direction.Right

    //   3 4
    //  2x x 6
    // 11x55
    //2x x6
    //3x77
    // 4
    val jumps =
        from(downTo(99, 50), fromTo(49, 49), listOf50(100), downTo(49, 0), Direction.Down, Direction.Left) + // 1
                from(
                    fromTo(99, 99),
                    downTo(49, 0),
                    downTo(99, 50),
                    listOf50(50),
                    Direction.Right,
                    Direction.Up
                ) + // inv 1
                from(
                    fromTo(100, 149),
                    fromTo(-1, -1),
                    downTo(49, 0),
                    listOf50(50),
                    Direction.Right,
                    Direction.Left
                ) + // 2
                from(
                    downTo(49, 0),
                    fromTo(49, 49),
                    fromTo(100, 149),
                    listOf50(0),
                    Direction.Right,
                    Direction.Left
                ) + // inv 2
                from(
                    fromTo(150, 199),
                    downTo(-1, -1),
                    listOf50(0),
                    fromTo(50, 99),
                    Direction.Down,
                    Direction.Left
                ) + // 3
                from(
                    fromTo(-1, -1),
                    fromTo(50, 99),
                    fromTo(150, 199),
                    listOf50(0),
                    Direction.Right,
                    Direction.Up
                ) + // inv 3
                from(
                    fromTo(200, 200),
                    fromTo(0, 49),
                    listOf50(0),
                    fromTo(100, 149),
                    Direction.Down,
                    Direction.Down
                ) + // 4
                from(
                    fromTo(-1, -1),
                    fromTo(100, 149),
                    listOf50(199),
                    fromTo(0, 49),
                    Direction.Up,
                    Direction.Up
                ) + // inv 4
                from(
                    downTo(199, 150),
                    fromTo(50, 50),
                    listOf50(149),
                    downTo(99, 49),
                    Direction.Up,
                    Direction.Right
                ) + // 7
                from(
                    fromTo(150, 150),
                    downTo(99, 50),
                    downTo(199, 150),
                    listOf50(49),
                    Direction.Left,
                    Direction.Down
                ) + // inv 7
                from(
                    downTo(149, 100),
                    fromTo(100, 100),
                    fromTo(0, 49),
                    listOf50(149),
                    Direction.Left,
                    Direction.Right
                ) + // 6
                from(
                    fromTo(0, 49),
                    fromTo(150, 150),
                    downTo(149, 100),
                    listOf50(99),
                    Direction.Left,
                    Direction.Right
                ) + // inv 6
                from(
                    fromTo(50, 99),
                    fromTo(100, 100),
                    listOf50(49).toList(),
                    fromTo(100, 149),
                    Direction.Up,
                    Direction.Right
                ) + // 5
                from(
                    (50..50).toList(),
                    (100..149).toList(),
                    (50..99).toList(),
                    listOf50(99),
                    Direction.Left,
                    Direction.Down
                ) // inv5

    for (instruction in instructions) {
        when (instruction) {
            Turn.Left -> {
                direction = Direction.values()[(direction.ordinal - 1 + 4) % 4]
            }
            Turn.Right -> {
                direction = Direction.values()[(direction.ordinal + 1) % 4]
            }
            is Move -> {
                for (move in 0 until instruction.step) {
                    val newPos = when (direction) {
                        Direction.Right -> map.nextPositionCube(pos, dCol = 1, jumps = jumps, dir = direction)
                        Direction.Down -> map.nextPositionCube(pos, dRow = 1, jumps = jumps, dir = direction)
                        Direction.Left -> map.nextPositionCube(pos, dCol = -1, jumps = jumps, dir = direction)
                        Direction.Up -> map.nextPositionCube(pos, dRow = -1, jumps = jumps, dir = direction)
                    }
                    if (map[newPos.first]!!.type == TileType.Wall) {
                        break
                    } else {
                        if (newPos.second != null) {
                            direction = newPos.second!!
                        }
                        pos = newPos.first
                    }
                }
            }
        }
    }

    return (pos.row + 1) * 1000 + (pos.col + 1) * 4 + direction.ordinal
}

private fun fromTo(from: Int, to: Int) = (from..to).toList()
private fun downTo(from: Int, to: Int) = (from downTo to).toList()

private fun listOf50(num: Int) = generateSequence { num }.take(50).toList()

fun Map<Coordinate, Tile>.nextPositionCube(
    pos: Coordinate,
    dir: Direction,
    dRow: Int = 0,
    dCol: Int = 0,
    jumps: Map<Pair<Coordinate, Direction>, Pair<Coordinate, Direction>>
): Pair<Coordinate, Direction?> {
    val newPos = pos.copy(row = pos.row + dRow, col = pos.col + dCol)
    return if (containsKey(newPos)) {
        Pair(newPos, null)
    } else {
        jumps[Pair(newPos, dir)]!!
    }
}

fun Map<Coordinate, Tile>.nextPosition(pos: Coordinate, dRow: Int = 0, dCol: Int = 0): Coordinate {
    val newPos = pos.copy(row = pos.row + dRow, col = pos.col + dCol)
    return if (containsKey(newPos)) {
        newPos
    } else {
        return if (dRow != 0) {
            val col = filter { it.key.col == pos.col }
            if (dRow > 0) {
                col.minByOrNull { it.key.row }!!.key
            } else {
                col.maxByOrNull { it.key.row }!!.key
            }
        } else {
            val row = filter { it.key.row == pos.row }
            if (dCol > 0) {
                row.minByOrNull { it.key.col }!!.key
            } else {
                row.maxByOrNull { it.key.col }!!.key
            }
        }
    }
}

fun readInput(input: List<String>): Pair<Map<Coordinate, Tile>, List<Instruction>> {
    val map = input.dropLast(2).flatMapIndexed { row, line ->
        line.mapIndexedNotNull { col, tile ->
            when (tile) {
                ' ' -> null
                '.' -> Tile(row, col, TileType.Free)
                '#' -> Tile(row, col, TileType.Wall)
                else -> throw Exception("invalid type")
            }
        }
    }.associateBy { Coordinate(it.row, it.col) }

    val instructions = mutableListOf<Instruction>()
    var number = 0

    for (instruction in input.last()) {
        number = when (instruction) {
            'L' -> {
                instructions.add(Move(number))
                instructions.add(Turn.Left)
                0
            }
            'R' -> {
                instructions.add(Move(number))
                instructions.add(Turn.Right)
                0
            }
            else -> number * 10 + instruction.digitToInt()
        }
    }
    if (number > 0) {
        instructions.add(Move(number))
    }

    return Pair(map, instructions.toList())
}

enum class Direction {
    Right,
    Down,
    Left,
    Up
}

enum class TileType {
    Free,
    Wall,
}

data class Tile(val row: Int, val col: Int, val type: TileType)

sealed interface Instruction

enum class Turn : Instruction {
    Left,
    Right
}

data class Move(val step: Int) : Instruction