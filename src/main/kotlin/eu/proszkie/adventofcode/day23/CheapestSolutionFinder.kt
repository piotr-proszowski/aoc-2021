package eu.proszkie.adventofcode.day23

import java.util.PriorityQueue
import java.util.UUID

object CheapestSolutionFinder {
    fun findCheapestSolutionCost(burrowState: BurrowState): Int {
        val queue: PriorityQueue<BurrowState> = PriorityQueue(leastMinimalCostToFinalStateAndEnergyUsed())
        queue.add(burrowState)
        val alreadySeen: MutableMap<Pair<UUID, BurrowStateChange>, Int> = HashMap()
        val burrowToAmount: MutableMap<UUID, Int> = HashMap()

        var cheapest = Int.MAX_VALUE

        while (queue.isNotEmpty()) {
            val state = queue.poll()
            if (alreadySeen.containsKey(state.burrow.burrowId to state.history.last())
                && alreadySeen[state.burrow.burrowId to state.history.last()]!! <= state.energyUsed
            ) {
                burrowToAmount[state.burrow.burrowId] = (burrowToAmount[state.burrow.burrowId] ?: 0) + 1
                continue
            } else {
                alreadySeen[state.burrow.burrowId to state.history.last()] = state.energyUsed
            }

            if (state.isFinal()) {
                if(state.energyUsed < cheapest) {
                    cheapest = state.energyUsed
                }
            }

            val nextStates = state.findNextValidStates()
            queue.addAll(nextStates)
        }

        return cheapest
    }

    private fun leastMinimalCostToFinalStateAndEnergyUsed(): Comparator<BurrowState> = Comparator { a, b ->
        leastMinimalCostToFinalStateAndEnergyUsed(a, b)
    }

    private fun leastMinimalCostToFinalStateAndEnergyUsed(a: BurrowState, b: BurrowState): Int {
        val leastMinimalCostToFinalState = a.minimalCostToFinalState.compareTo(b.minimalCostToFinalState)
        val leastEnergyUsed = a.energyUsed.compareTo(b.energyUsed)
        return if (leastMinimalCostToFinalState == 0) {
            leastEnergyUsed
        } else {
            leastMinimalCostToFinalState
        }
    }
}