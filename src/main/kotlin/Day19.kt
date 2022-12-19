import java.io.File
import java.util.*

val resourceRegex = Regex("([0-9]+) (.+)")

fun main() {
    val blueprintRegex =
        Regex("Blueprint ([0-9]+): Each ore robot costs (.*?)\\. Each clay robot costs (.*?)\\. Each obsidian robot costs (.*?)\\. Each geode robot costs (.*?)\\.")

    val blueprints = File("inputs/day19.txt").readLines().map { blueprint ->
        val (id, oreRobot, clayRobot, obsidianRobot, geodeRobot) = blueprintRegex.find(blueprint)!!.destructured

        Blueprint(
            id.toInt(), mapOf(
                Resource.Ore to readResources(oreRobot),
                Resource.Clay to readResources(clayRobot),
                Resource.Obsidian to readResources(obsidianRobot),
                Resource.Geode to readResources(geodeRobot),
            )
        )
    }

    println(blueprints.sumOf { runSimulation(it, 24) * it.id })
    println(blueprints.take(3).fold(1) { e, blueprint -> e * runSimulation(blueprint, 32) })
}

fun runSimulation(blueprint: Blueprint, rounds: Int): Int {
    val queue = PriorityQueue(compareByDescending<MiningState> { it.getSpeed(Resource.Geode) })
    queue.addAll(blueprint.createBuilds(MiningState(0, emptyMap(), mapOf(Resource.Ore to 1), Resource.Ore)))

    var max = -1

    val seen = mutableMapOf<Triple<Map<Resource, Int>, Map<Resource, Int>, Resource>, Int>()

    while (queue.isNotEmpty()) {
        val state = queue.poll()

        val key = Triple(state.speed, state.resources, state.nextToBuild)
        val duplicate = seen[key]

        if (state.round == rounds) {
            max = max.coerceAtLeast(state.getResource(Resource.Geode))
            continue
        }

        if ((duplicate != null && duplicate <= state.round) || state.maxGeodes(rounds) <= max) {
            continue
        }

        seen[key] = state.round

        if (state.canBuild(blueprint)) {
            val toBuild = blueprint.getRobotResources(state.nextToBuild)
            val existingSpeed = state.getSpeed(state.nextToBuild)

            val minusBuild = state.copy(
                round = state.round + 1,
                resources = state.speed.mapValues { (resource, v) ->
                    v + state.getResource(resource) - toBuild.getOrDefault(resource, 0)
                },
                speed = state.speed + (state.nextToBuild to existingSpeed + 1)
            )

            val newBuilds = blueprint.createBuilds(minusBuild)
            queue.addAll(newBuilds)
        } else {
            queue.add(state.copy(
                round = state.round + 1,
                resources = state.speed.mapValues { (k, v) -> v + state.getResource(k) }
            ))
        }
    }

    return max
}

private fun readResources(input: String): Map<Resource, Int> =
    input.split("and").associate { resource ->
        val (amount, type) = resourceRegex.find(resource)!!.destructured
        Resource.valueOf(type.trim().replaceFirstChar { it.uppercaseChar() }) to amount.toInt()
    }

enum class Resource {
    Ore,
    Clay,
    Obsidian,
    Geode
}

data class MiningState(
    val round: Int,
    val resources: Map<Resource, Int>,
    val speed: Map<Resource, Int>,
    val nextToBuild: Resource
) {
    fun getResource(resource: Resource): Int = resources.getOrDefault(resource, 0)

    fun getSpeed(resource: Resource): Int = speed.getOrDefault(resource, 0)

    fun canBuild(blueprint: Blueprint): Boolean {
        return blueprint.getRobotResources(nextToBuild)
            .all { (resource, v) -> getResource(resource) >= v }
    }

    fun maxGeodes(rounds: Int): Int {
        val remaining = rounds - round

        return getResource(Resource.Geode) +
                (getSpeed(Resource.Geode) * remaining) +
                ((remaining * (remaining + 1)) / 2)
    }
}

data class Blueprint(val id: Int, val robots: Map<Resource, Map<Resource, Int>>) {
    fun createBuilds(state: MiningState): List<MiningState> {
        val canBuild = robots
            .filter { (_, v) ->
                state.speed.keys.containsAll(v.keys)
            }

        if (canBuild.contains(Resource.Geode) && state.copy(nextToBuild = Resource.Geode).canBuild(this)) {
            return listOf(state.copy(nextToBuild = Resource.Geode))
        }

        return canBuild
            .filter { (k, _) ->
                val maxAmountToBuild = robots.values.maxOf { it.getOrDefault(k, 0) }
                val max = state.getSpeed(k) < maxAmountToBuild
                max || k == Resource.Geode
            }
            .map { state.copy(nextToBuild = it.key) }
    }

    fun getRobotResources(resource: Resource): Map<Resource, Int> =
        robots[resource] ?: throw Exception("Robot not found")
}