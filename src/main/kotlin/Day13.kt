import java.io.File
import java.util.*

fun main() {
    val pairs = createPairs()
    println(part1(pairs))
    println(part2(pairs))
}

private fun part1(pairs: List<Pair<Seq, Seq>>) =
    pairs.withIndex()
        .filter { (_, p) -> p.first.isInOrder(p.second) == true }
        .sumOf { it.index + 1 }

private fun part2(pairs: List<Pair<Seq, Seq>>): Int {
    val firstDivider = Seq(mutableListOf(Seq(mutableListOf(Num(2)))))
    val secondDivider = Seq(mutableListOf(Seq(mutableListOf(Num(6)))))
    val withDividers = pairs.flatMap { it.toList() } + firstDivider + secondDivider

    return withDividers
        .sortedWith { f, s -> if (f.isInOrder(s) == true) -1 else 1 }
        .withIndex()
        .filter { it.value == firstDivider || it.value == secondDivider }
        .fold(1) { f, s -> f * (s.index + 1) }
}

private fun createPairs(): List<Pair<Seq, Seq>> {
    val input = File("inputs/day13.txt").readText()
    val pairs = input.split("\r\n\r\n").map { pair ->
        val (top, bottom) = pair.split("\r\n").map { parse(it) }
        Pair(top, bottom)
    }
    return pairs
}

fun parse(input: String): Seq {
    val stack = Stack<Seq>()
    var num: Num? = null

    for (char in input) {
        when (char) {
            '[' -> stack.push(Seq(mutableListOf()))
            ']' -> {
                if (num != null) stack.peek().add(num)
                num = null

                val top = stack.pop()
                if (stack.isEmpty()) {
                    return top
                }
                stack.peek().add(top)
            }
            ',' -> {
                if (num != null) stack.peek().add(num)
                num = null
            }
            else -> num = num?.add(char.digitToInt()) ?: Num(char.digitToInt())
        }
    }

    throw Exception("Not balanced stack")
}

sealed interface Item {
    fun isInOrder(other: Item): Boolean? {
        if (this is Num && other is Num) {
            return this.compareTo(other)
        }

        val items = this.toSeq()
        val otherItems = other.toSeq()

        val inOrder = items
            .withIndex()
            .map { (index, left) ->
                when (val right = otherItems.getOrNull(index)) {
                    null -> false
                    else -> left.isInOrder(right)
                }
            }.firstOrNull { it != null }

        if (inOrder != null) return inOrder
        return if (items.size == otherItems.size) null else items.size < otherItems.size
    }

    fun toSeq() = when (this) {
        is Num -> mutableListOf(this)
        is Seq -> this.list
    }
}

data class Num(var num: Int) : Item {
    fun add(digit: Int): Num = Num(num * 10 + digit)

    fun compareTo(other: Num): Boolean? =
        if (this.num == other.num) null else this.num < other.num
}

data class Seq(val list: MutableList<Item>) : Item {
    fun add(item: Item) {
        list.add(item)
    }
}