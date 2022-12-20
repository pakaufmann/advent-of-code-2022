import java.io.File

fun main() {
    val input = File("inputs/day20.txt").readLines().map { it.toInt() }

    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<Int>): Long {
    val (start, length) = createLinkedList(input)
    runMix(start, length)
    return getNumber(start, length)
}

private fun part2(input: List<Int>): Long {
    val (start, length) = createLinkedList(input, 811589153)
    repeat(10) { runMix(start, length) }
    return getNumber(start, length)
}

private fun getNumber(newStart: Node, length: Long): Long {
    val list = mutableListOf<Long>()

    var current: Node? = newStart
    var index = 0L
    var zeroIndex = -1L

    while (current != newStart || zeroIndex == -1L) {
        list.add(current!!.num)
        if (current.num == 0L) {
            zeroIndex = index
        }
        index++
        current = current.next
    }

    val first = list[((zeroIndex + 1000) % length).toInt()]
    val second = list[((zeroIndex + 2000) % length).toInt()]
    val third = list[((zeroIndex + 3000) % length).toInt()]
    return first + second + third
}

private fun runMix(start: Node, length: Long) {
    var cur: Node? = start

    while (cur != null) {
        if (cur.num > 0) {
            for (i in 0 until cur.num % (length - 1)) {
                val prev = cur.prev
                val next = cur.next

                cur.next = next?.next
                cur.prev = next

                next?.next?.prev = cur
                next?.prev = prev
                next?.next = cur

                prev?.next = next
            }
        } else {
            for (i in (cur.num % (length - 1)) until 0) {
                val prev = cur.prev
                val next = cur.next

                cur.next = prev
                cur.prev = prev?.prev

                prev?.prev?.next = cur
                prev?.next = next
                prev?.prev = cur

                next?.prev = prev
            }
        }

        cur = cur.originalNext
    }
}

private fun createLinkedList(input: List<Int>, multiply: Long = 1): Pair<Node, Long> {
    var prev: Node? = null
    var start: Node? = null

    for (num in input) {
        val cur = Node(num * multiply, null, prev, null)
        if (start == null) {
            start = cur
        }
        prev?.originalNext = cur
        prev?.next = cur
        prev = cur
    }

    start!!.prev = prev
    prev!!.next = start

    return Pair(start, input.size.toLong())
}

data class Node(val num: Long, var originalNext: Node?, var prev: Node?, var next: Node?)