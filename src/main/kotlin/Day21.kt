import java.io.File

fun main() {
    println(part1(readMonkeys()))
    println(part2(readMonkeys()))
}

private fun part1(monkeys: MutableMap<String, MathMonkey>): Long? {
    return runCalculation(monkeys)
}

private fun part2(monkeys: Map<String, MathMonkey>): Long {
    val first = runCalculation(monkeys)
    val second = runCalculation(monkeys.setHuman(monkeys.getHuman() + 2))

    val up = first.compareTo(second).toLong()

    val rootMonkey = monkeys["root"]!!
    val compareRoot =
        monkeys + ("root" to rootMonkey.copy(calculation = (rootMonkey.calculation as Calc).copy(op = "=")))

    var lower = Long.MIN_VALUE / 1000
    var upper = Long.MAX_VALUE / 1000

    while (true) {
        val middle = lower + ((upper - lower) / 2)
        val changedHuman = compareRoot.setHuman(middle)

        when (runCalculation(changedHuman)) {
            0L -> {
                val checkLower = runCalculation(compareRoot.setHuman(middle - 1))
                return if (checkLower == 0L) middle - 1 else middle
            }
            up -> lower = middle
            else -> upper = middle
        }
    }
}

fun Map<String, MathMonkey>.setHuman(value: Long): Map<String, MathMonkey> =
    this + ("humn" to this["humn"]!!.copy(calculation = Numb(value)))

fun Map<String, MathMonkey>.getHuman(): Long =
    (this["humn"]!!.calculation as Numb).number

private fun readMonkeys() =
    File("inputs/day21.txt").readLines()
        .map { monkey ->
            val parts = monkey.split(": ")
            when (val num = parts[1].toLongOrNull()) {
                null -> {
                    val opParts = parts[1].split(" ")
                    MathMonkey(parts[0], Calc(opParts[0], opParts[2], opParts[1]))
                }
                else -> MathMonkey(parts[0], Numb(num))
            }
        }.associateBy { it.name }
        .toMutableMap()

private fun runCalculation(monkeys: Map<String, MathMonkey>): Long {
    val numbers = monkeys
        .filter { it.value.calculation is Numb }
        .mapValues { (it.value.calculation as Numb).number }
        .toMutableMap()

    var remainingCalculations = monkeys
        .filter { it.value.calculation is Calc }
        .mapValues { it.value.calculation as Calc }

    while (remainingCalculations.isNotEmpty()) {
        val unresolved = mutableMapOf<String, Calc>()

        for ((monkeyName, calc) in remainingCalculations) {
            val first = numbers[calc.first]
            val second = numbers[calc.second]

            if (first != null && second != null) {
                numbers[monkeyName] = when (calc.op) {
                    "+" -> first + second
                    "-" -> first - second
                    "*" -> first * second
                    "/" -> first / second
                    "=" -> first.compareTo(second).toLong()
                    else -> throw Exception("invalid op")
                }
            } else {
                unresolved[monkeyName] = calc
            }
        }

        remainingCalculations = unresolved.toMap()
    }
    return numbers["root"]!!
}

sealed interface Calculation

data class Numb(val number: Long) : Calculation

data class Calc(val first: String, val second: String, val op: String) : Calculation

data class MathMonkey(val name: String, val calculation: Calculation)