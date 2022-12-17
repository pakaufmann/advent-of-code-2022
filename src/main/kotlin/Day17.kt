import java.io.File

fun main() {
    val windDirections = File("inputs/day17.txt").readLines()[0]

    println(determineHeight(windDirections, 2022))
    println(determineHeight(windDirections, 1000000000000))
}

private fun determineHeight(windDirections: String, rounds: Long): Long {
    val shapes = listOf(
        listOf(Block(2, 0), Block(3, 0), Block(4, 0), Block(5, 0)),
        listOf(Block(3, 0), Block(2, 1), Block(3, 1), Block(4, 1), Block(3, 2)),
        listOf(Block(2, 0), Block(3, 0), Block(4, 0), Block(4, 1), Block(4, 2)),
        listOf(Block(2, 0), Block(2, 1), Block(2, 2), Block(2, 3)),
        listOf(Block(2, 0), Block(3, 0), Block(2, 1), Block(3, 1)),
    )

    val existingBlocks = mutableSetOf<Block>()
    var height = 0
    var wind = 0

    var top = listOf(
        Block(0, -1),
        Block(1, -1),
        Block(2, -1),
        Block(3, -1),
        Block(4, -1),
        Block(5, -1),
        Block(6, -1)
    )

    val repeatHashes = mutableMapOf<RepeatHash, Pair<Long, Int>>()

    var round = 0L
    var repeatedHeight = 0L

    while (round < rounds) {
        val shapeType = (round % 5).toInt()

        val repeatHash = RepeatHash(wind, shapeType, top.normalize())
        val previousHash = repeatHashes[repeatHash]

        if (previousHash != null) {
            repeatHashes.clear()
            val (heightToAdd, roundsToAdd) = calculateRepeat(previousHash, round, rounds, height)
            repeatedHeight = heightToAdd
            round += roundsToAdd
            continue
        }

        repeatHashes[repeatHash] = Pair(round, height)

        val newShape = shapes[shapeType].moveVertically(height + 3)
        val (shapeEndState, updatedWind) = placeShape(windDirections, wind, newShape, existingBlocks)

        wind = updatedWind
        height = height.coerceAtLeast(shapeEndState.maxOf { it.y } + 1)
        top = top.updateTops(shapeEndState)

        existingBlocks.addAll(shapeEndState)
        round++
    }

    return height + repeatedHeight
}

private fun placeShape(
    windDirections: String, wind: Int, blocks: List<Block>, existingBlocks: MutableSet<Block>
): Pair<List<Block>, Int> {
    var windState = wind
    var blockState = blocks
    while (true) {
        val movedBlocks = when (windDirections[windState]) {
            '>' -> blockState.moveRight()
            else -> blockState.moveLeft()
        }
        blockState = if (movedBlocks.any { existingBlocks.contains(it) }) {
            blockState
        } else {
            movedBlocks
        }

        windState = (windState + 1) % windDirections.length

        val newBlocks = blockState.moveVertically(-1)
        if (newBlocks.any { existingBlocks.contains(it) } || newBlocks.any { it.y == -1 }) {
            break
        } else {
            blockState = newBlocks
        }
    }
    return Pair(blockState, windState)
}

private fun calculateRepeat(
    existingState: Pair<Long, Int>,
    round: Long,
    rounds: Long,
    height: Int,
): Pair<Long, Long> {
    val (prevTurn, prevHeight) = existingState

    val repeatLength = round - prevTurn
    val remaining = rounds - round

    val heightDiff = height - prevHeight
    val repeats = remaining / repeatLength

    return Pair(heightDiff * repeats, repeats * repeatLength)
}


private fun List<Block>.normalize() =
    if (all { it.y > 0 }) {
        val min = minOf { it.y }
        map { it.copy(y = it.y - min) }
    } else {
        this
    }

private fun List<Block>.updateTops(blocks: List<Block>) =
    map { currentTop ->
        val potentialNew = blocks.filter { it.x == currentTop.x }.maxByOrNull { it.y }
        potentialNew?.copy(y = potentialNew.y) ?: currentTop
    }

fun List<Block>.moveVertically(offset: Int): List<Block> {
    return this.map { it.copy(y = it.y + offset) }
}

fun List<Block>.moveRight(): List<Block> {
    if (any { it.x == 6 }) return this
    return this.map { it.copy(x = it.x + 1) }
}

fun List<Block>.moveLeft(): List<Block> {
    if (any { it.x == 0 }) return this
    return this.map { it.copy(x = it.x - 1) }
}

data class Block(val x: Int, val y: Int)

data class RepeatHash(val wind: Int, val shape: Int, val top: List<Block>)
