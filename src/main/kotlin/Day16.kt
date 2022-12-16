import java.io.File
import java.util.*
import kotlin.system.measureTimeMillis

fun main() {
    val valveRegex = Regex("Valve ([A-Z]+) has flow rate=([0-9]+); tunnel(s|) lead(s|) to (valve|valves) (.*)")

    val valves = reduceValveList(File("inputs/day16.txt").readLines().map { line ->
        val (name, flowRate, _, _, _, to) = valveRegex.find(line)!!.destructured
        name to Valve(name, flowRate.toInt(), to.split(", "))
    }.toMap())

    println("Time:" + measureTimeMillis { println(part1(valves)) })
    println(part2(valves))
}

private fun part1(valves: Map<String, Valve>): Int {
    return findMax(valves, valves.values.toSet() - valves["AA"]!!, 30)
}

private fun part2(valves: Map<String, Valve>): Int {
    val queue = PriorityQueue(compareBy<ValveState> { it.time })
    queue.add(ValveState(valves["AA"]!!, 0, 0, valves.values.toSet() - valves["AA"]!!))

    val pathsForFirst = mutableMapOf<Set<Valve>, Int>()

    while (!queue.isEmpty()) {
        val current = queue.poll()

        val existing = pathsForFirst[current.remaining]
        if (existing == null) {
            pathsForFirst[current.remaining] = current.totalPressure
        } else if (existing < current.totalPressure) {
            pathsForFirst[current.remaining] = current.totalPressure
        }

        if (current.remaining.isEmpty()) {
            continue
        }

        if (current.time >= 26) {
            continue
        }

        queue.addAll(current.remaining
            .map { next ->
                val step = current.at.toValves[next.name]!!
                val toTime = (current.time + step + 1).coerceAtMost(26)
                val newPressure = next.flowRate * (26 - toTime)
                current.copy(
                    at = valves[next.name]!!,
                    time = toTime,
                    totalPressure = current.totalPressure + newPressure,
                    remaining = current.remaining - valves[next.name]!!
                )
            })
    }

    var maxReleased = 0


    for ((remaining, previousTotal) in pathsForFirst) {
        maxReleased = maxReleased.coerceAtLeast(previousTotal + findMax(valves, remaining, 26))
    }

    return maxReleased
}

private fun findMax(valves: Map<String, Valve>, remaining: Set<Valve>, rounds: Int): Int {
    val queue = PriorityQueue(compareBy<ValveState> { it.time })
    queue.add(ValveState(valves["AA"]!!, 0, 0, remaining))

    val visited = mutableSetOf<Pair<Valve, Set<Valve>>>()

    var maxReleased = 0

    while (!queue.isEmpty()) {
        val current = queue.poll()

        if (current.remaining.isEmpty()) {
            maxReleased = current.totalPressure.coerceAtLeast(maxReleased)
            continue
        }

        val key = Pair(current.at, current.remaining)

        if (visited.contains(key)) {
            continue
        }
        visited.add(key)

        if (current.time >= rounds) {
            maxReleased = current.totalPressure.coerceAtLeast(maxReleased)
            continue
        }

        queue.addAll(current.remaining
            .map { next ->
                val step = current.at.toValves[next.name]!!
                val toTime = (current.time + step + 1).coerceAtMost(rounds)
                val newPressure = next.flowRate * (rounds - toTime)
                current.copy(
                    at = valves[next.name]!!,
                    time = toTime,
                    totalPressure = current.totalPressure + newPressure,
                    remaining = current.remaining - valves[next.name]!!
                )
            }
        )
    }

    return maxReleased
}

fun reduceValveList(valves: Map<String, Valve>): Map<String, Valve> {
    val result = mutableMapOf<String, Valve>()

    for ((name, valve) in valves) {
        if (valve.flowRate == 0 && valve.name != "AA") continue

        val stepsTo = mutableMapOf<String, Int>()

        val queue = PriorityQueue<Pair<Valve, Int>>(compareBy { it.second })
        queue.add(Pair(valve, 0))
        val visited = mutableSetOf<Valve>()

        while (queue.isNotEmpty()) {
            val (current, steps) = queue.poll()

            if (visited.contains(current)) continue
            visited.add(current)

            if (current.flowRate > 0 && current != valve) {
                stepsTo[current.name] = steps
            }

            queue.addAll(current.to.map { Pair(valves[it]!!, steps + 1) })
        }

        result[name] = valve.copy(toValves = stepsTo)
    }

    return result
}

data class ValveState(
    val at: Valve,
    val totalPressure: Int,
    val time: Int,
    val remaining: Set<Valve>
)

data class Valve(
    val name: String,
    val flowRate: Int,
    val to: List<String>,
    val toValves: Map<String, Int> = emptyMap()
) {
    override fun equals(other: Any?): Boolean {
        if (other !is Valve) return false

        return other.name == this.name
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + flowRate
        result = 31 * result + to.hashCode()
        result = 31 * result + toValves.hashCode()
        return result
    }
}