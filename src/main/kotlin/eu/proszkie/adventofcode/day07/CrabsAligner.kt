package eu.proszkie.adventofcode.day07

import kotlin.math.abs

class CrabsAligner {
    fun calculateOptimalHorizontalPosition(positions: List<Int>): Int {
        val sortedPositions = positions.sorted()
        val median = positions.getMiddleElement()
        val average = sortedPositions.average().toInt()

        return average + 1
    }

    private fun List<Int>.getMiddleElement() = this[this.size / 2]

    fun calculateTheLeastFuelNeeded(positions: List<Int>): Int {
        val min = positions.minOrNull()!!
        val max = positions.maxOrNull()!!
        return (min..max).map { calculateNeededFuelToMoveIntoPosition(positions, it) }
            .sorted()
            .first()
    }

    fun calculateNeededFuelToMoveIntoPosition(positions: List<Int>, optimalPosition: Int): Int {
        val costOfNextStep = generateSequence(1) { it + 1 }

        return positions.sumOf {
            val howManySteps = abs(optimalPosition - it)
            costOfNextStep.take(howManySteps).toList().sum()
        }
    }
}