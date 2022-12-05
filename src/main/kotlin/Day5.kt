import java.io.File
import java.util.*

fun main() {
    val input = File("inputs/day5.txt").readLines()
    println(run(input) { from, to, amount -> from.moveIndividually(to, amount) })
    println(run(input) { from, to, amount -> from.moveTogether(to, amount) })
}

private fun run(lines: List<String>, moveFunction: (Stack<Char>, Stack<Char>, Int) -> Unit): String {
    val (stacks, movements) = createStacks(lines)
    return stacks.move(movements, moveFunction).top()
}

private fun Array<Stack<Char>>.move(
    movements: List<String>,
    move: (Stack<Char>, Stack<Char>, Int) -> Unit
): Array<Stack<Char>> {
    val movementRegex = Regex("move ([0-9]+) from ([0-9]+) to ([0-9]+)")
    movements.forEach { movement ->
        val (amount, from, to) = movementRegex.find(movement)!!.destructured
        move(this[from.toInt() - 1], this[to.toInt() - 1], amount.toInt())
    }
    return this
}

private fun Stack<Char>.moveIndividually(toStack: Stack<Char>, amount: Int) =
    repeat(amount) { toStack.push(pop()) }

private fun Stack<Char>.moveTogether(
    toStack: Stack<Char>,
    amount: Int
) {
    val intermediateStack = Stack<Char>()
    this.moveIndividually(intermediateStack, amount)
    intermediateStack.moveIndividually(toStack, amount)
}

private fun Array<Stack<Char>>.top() = joinToString(
    separator = "",
    transform = { it.peek().toString() }
)

private fun createStacks(lines: List<String>): Pair<Array<Stack<Char>>, List<String>> {
    val end = lines.indexOf("")
    val stackCount = lines[end - 1][lines[end - 1].length - 2].digitToInt()

    val stacks = Array<Stack<Char>>(stackCount) { Stack() }

    for ((stack, index) in (1..lines.first().length step 4).withIndex()) {
        for (line in (end - 2) downTo 0) {
            val crate = lines[line][index]
            if (crate == ' ') break
            stacks[stack].push(crate)
        }

    }
    return Pair(stacks, lines.drop(end + 1))
}