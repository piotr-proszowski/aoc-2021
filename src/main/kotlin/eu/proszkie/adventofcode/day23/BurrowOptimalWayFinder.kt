package eu.proszkie.adventofcode.day23

import java.util.PriorityQueue
import kotlin.math.min

object BurrowOptimalWayFinder {
    fun findTheLowestCostOfMovingToFinalConfiguration(burrowState: BurrowState): Long {
        val queue: PriorityQueue<BurrowState> = PriorityQueue() { a, b ->
            b.energyUsed.compareTo(a.energyUsed)
        }

        val uniqueStates: MutableMap<String, Long> = HashMap()
        queue.add(burrowState)

        var lowest: Long? = null

        while (!queue.isEmpty()) {
            val burrow = queue.poll()
            val stringRepresentation = burrow.burrow.toString()
            val leastEnergyUsedForGivenConfiguration =
                uniqueStates[stringRepresentation]?.let { min(burrow.energyUsed, it) }
            if (leastEnergyUsedForGivenConfiguration != null) {
                if (burrow.energyUsed > leastEnergyUsedForGivenConfiguration) {
                    continue
                }
            }

            uniqueStates[stringRepresentation] = burrow.energyUsed

            if (burrow.isFinal()) {
                if (lowest == null || lowest > burrow.energyUsed) {
                    lowest = burrow.energyUsed
                }
            }

            val nextStates = burrow.findPossibleChanges().asSequence()
                .map(burrow::applyChange)
                .filterIsInstance<Success>()
                .map(Success::changedBurrow)
                .filter { state -> lowest?.let { it > state.energyUsed } ?: true }

            nextStates.sortedBy(BurrowState::energyUsed).forEach(queue::add)
        }

        return lowest!!
    }
}