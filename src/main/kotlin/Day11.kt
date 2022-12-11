import java.io.File

fun main() {
    val input = File("inputs/day11.txt").readText()
    println(runFor(readMonkeys(input), 20, 3))
    println(runFor(readMonkeys(input), 10000, 1))
}

private fun readMonkeys(input: String): List<Monkey> {
    val startingItemsRegex = Regex("Starting items: (.*)")
    val operationRegex = Regex("Operation: new = (old|[0-9]+) ([*+]) (old|[0-9]+)")
    val divisibleRegex = Regex("Test: divisible by ([0-9]+)")
    val throwToRegex = Regex("throw to monkey ([0-9]+)")

    return input.split("\r\n\r\n").map { it.lines() }.map { monkey ->
        val (items) = startingItemsRegex.find(monkey[1])!!.destructured
        val (op1, operator, op2) = operationRegex.find(monkey[2])!!.destructured
        val operation = when (operator) {
            "*" -> times(toOp(op1), toOp(op2))
            "+" -> plus(toOp(op1), toOp(op2))
            else -> throw Exception("Invalid operation")
        }
        val (divNum) = divisibleRegex.find(monkey[3])!!.destructured
        val (monkeyTrue) = throwToRegex.find(monkey[4])!!.destructured
        val (monkeyFalse) = throwToRegex.find(monkey[5])!!.destructured

        Monkey(
            items.split(", ").map { it.toLong() }.toMutableList(), operation,
            divNum.toInt(),
            monkeyTrue.toInt(),
            monkeyFalse.toInt()
        )
    }
}

fun toOp(op: String): Operand {
    return when (op) {
        "old" -> Old
        else -> Number(op.toLong())
    }
}

fun times(op1: Operand, op2: Operand): (Long) -> Long = { old -> op1.run(old) * op2.run(old) }

fun plus(op1: Operand, op2: Operand): (Long) -> Long = { old -> op1.run(old) + op2.run(old) }

private fun runFor(monkeys: List<Monkey>, rounds: Int, divideBy: Int): Long {
    val mod = monkeys.fold(1) { f, s -> f * s.divisible }
    repeat(rounds) { monkeys.runRound(divideBy, mod) }

    return monkeys
        .sortedByDescending { it.inspectCount }
        .take(2)
        .fold(1) { f, s -> f * s.inspectCount }
}

fun List<Monkey>.runRound(divideBy: Int, mod: Int) {
    forEach { it.throwItems(this, divideBy, mod) }
}

sealed interface Operand {
    fun run(old: Long): Long
}

object Old : Operand {
    override fun run(old: Long): Long = old
}

data class Number(val num: Long) : Operand {
    override fun run(old: Long): Long = num

}

data class Monkey(
    val items: MutableList<Long>,
    val operation: (Long) -> Long,
    val divisible: Int,
    val onTrue: Int,
    val onFalse: Int
) {
    var inspectCount: Long = 0

    fun throwItems(monkeys: List<Monkey>, divideBy: Int, mod: Int) {
        inspectCount += items.size
        items.map { item -> throwItem(item, monkeys, divideBy, mod) }
        items.clear()
    }

    private fun throwItem(item: Long, monkeys: List<Monkey>, divideBy: Int, mod: Int) {
        val worryLevel = (operation(item) / divideBy) % mod
        if (worryLevel % divisible == 0L) {
            monkeys[onTrue].items += worryLevel
        } else {
            monkeys[onFalse].items += worryLevel
        }
    }
}