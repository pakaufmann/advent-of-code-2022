import java.io.File
import java.math.BigInteger
import kotlin.math.absoluteValue

fun main() {
    val lines = File("inputs/day15.txt").readLines()
    val sensors = readSensors(lines)
    println(part1(sensors))
    println(part2(sensors))
}

private fun part1(sensors: List<Sensor>, row: Int = 2000000): Int {
    val from = sensors.minOf { it.center.col - it.radius }
    val to = sensors.maxOf { it.center.col + it.radius }

    return (from..to).count { col ->
        sensors.any { it.reaches(row, col) && !(it.beacon.row == row && it.beacon.col == col) }
    }
}

private fun part2(sensors: List<Sensor>, upper: Int = 4000000): BigInteger? {
    for (row in 0..upper) {
        var col = 0
        while (col <= upper) {
            val reach = sensors.find { it.reaches(row, col) }
                ?: return col.toBigInteger() * upper.toBigInteger() + row.toBigInteger()

            val rowOffset = reach.center.row - row
            val colOffset = reach.center.col - col

            val jump = reach.radius - rowOffset.absoluteValue + colOffset
            col += jump + 1
        }
    }

    return null
}

private fun readSensors(lines: List<String>): List<Sensor> {
    val lineRegex = Regex("Sensor at x=(-?[0-9+]+), y=(-?[0-9+]+): closest beacon is at x=(-?[0-9+]+), y=(-?[0-9+]+)")

    val sensors = lines.map { line ->
        val (sensorX, sensorY, beaconX, beaconY) = lineRegex.find(line)!!.destructured

        Sensor.fromCoordinates(
            Coordinate(sensorY.toInt(), sensorX.toInt()),
            Coordinate(beaconY.toInt(), beaconX.toInt())
        )
    }
    return sensors
}

data class Sensor(val center: Coordinate, val radius: Int, val beacon: Coordinate) {
    fun reaches(row: Int, col: Int): Boolean {
        val dRow = (center.row - row).absoluteValue
        val dCol = (center.col - col).absoluteValue
        return dCol + dRow <= radius
    }

    companion object {
        fun fromCoordinates(center: Coordinate, beacon: Coordinate): Sensor {
            val dCol = (center.col - beacon.col).absoluteValue
            val dRow = (center.row - beacon.row).absoluteValue

            val radius = dRow + dCol
            return Sensor(center, radius, beacon)
        }
    }
}