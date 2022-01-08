package eu.proszkie.adventofcode.day19

import kotlin.math.abs

object ManhattanDistanceCalculator {
    fun calculate(from: Point, to: Point): Int {
        return abs(from.x - to.x) + abs(from.y - to.y) + abs(from.z - to.z)
    }
}