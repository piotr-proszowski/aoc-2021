package eu.proszkie.adventofcode.day23.awfulapproach

data class BurrowState(val burrow: Burrow, val energyUsed: Int = 0, val history: List<BurrowStateChange> = listOf()) {
    val amountOfFinalRooms = burrow.amountOfFinalRooms()
    val minimalCostToFinalState = minimalCostToReachDesiredState()

    fun findNextValidStates(): Collection<BurrowState> {
        return burrow.allChangeCandidates()
            .map(this::applyChange)
            .filterIsInstance<BurrowStateChangeSucceeded>()
            .map(BurrowStateChangeSucceeded::burrowState)
    }

    fun revertChange(): BurrowState {
        return copy(
            burrow = burrow.revert(history.last()),
            energyUsed = energyUsed - history.last().movable.energyNeededToMove,
            history = history.dropLast(1)
        )
    }

    private fun applyChange(change: BurrowStateChange): BurrowStateChangeResult {
        return when (val burrowChange = burrow.applyChange(change, history)) {
            is BurrowChangeSucceeded -> toBurrowStateChangeSucceeded(burrowChange, change)
            is BurrowChangeFailed -> BurrowStateChangeFailed(burrowChange)
        }
    }

    private fun toBurrowStateChangeSucceeded(
        burrowChange: BurrowChangeSucceeded,
        change: BurrowStateChange
    ) = BurrowStateChangeSucceeded(
        copy(
            burrow = burrowChange.burrow,
            energyUsed = energyUsed + change.movable.energyNeededToMove,
            history = history + change
        )
    )

    fun applyChanges(changes: List<BurrowStateChange>): BurrowStateChangeResult {
        return changes.fold(BurrowStateChangeSucceeded(this) as BurrowStateChangeResult) { result, change ->
            when (result) {
                is BurrowStateChangeSucceeded -> result.burrowState.applyChange(change)
                is BurrowStateChangeFailed -> result
            }
        }
    }

    override fun toString(): String {
        return "$burrow"
    }


    fun isFinal() = burrow.isFinal()

    private fun minimalCostToReachDesiredState(): Int {
        return burrow.minimalCostToReachDesiredState()
    }
}