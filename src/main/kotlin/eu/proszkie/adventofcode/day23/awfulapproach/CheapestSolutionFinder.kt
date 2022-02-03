package eu.proszkie.adventofcode.day23.awfulapproach

import java.util.PriorityQueue

object CheapestSolutionFinder {
    fun findCheapestSolutionCost(burrowState: BurrowState): Int {
        val queue: PriorityQueue<BurrowState> = PriorityQueue(leastMinimalCostToFinalState())
        val alreadySeen: MutableMap<Burrow, Int> = HashMap()
        queue.add(burrowState)

        var cheapest = Int.MAX_VALUE
        var duplicates = 0

        while (queue.isNotEmpty()) {
            val state = queue.poll()
            if (alreadySeen.containsKey(state.burrow) && alreadySeen[state.burrow]!! < state.energyUsed) {
                duplicates += 1
                continue
            }
            alreadySeen[state.burrow] = state.energyUsed
            if (state.isFinal() && state.energyUsed < cheapest) {
                cheapest = state.energyUsed
            }

            val nextStates = state.findNextValidStates()
                .filter { it.energyUsed + it.minimalCostToFinalState < cheapest }
            queue.addAll(nextStates)
        }

        return cheapest
    }

    private fun leastMinimalCostToFinalState(): Comparator<BurrowState> = Comparator { a, b ->
        leastMinimalCostToFinalState(a, b)
    }

    private fun leastMinimalCostToFinalState(a: BurrowState, b: BurrowState): Int {
        return a.minimalCostToFinalState.compareTo(b.minimalCostToFinalState)
    }
}