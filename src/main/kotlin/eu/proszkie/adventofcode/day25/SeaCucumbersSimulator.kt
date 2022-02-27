package eu.proszkie.adventofcode.day25

object SeaCucumbersSimulator {
    fun howManyIterationsToFinalState(initialState: SeaCucumbersState): Long =
        generateSequence(NotFinalStateWithCounter(initialState, 0) as StateWithCounter) {
            val state = it.state
            val counter = it.counter
            val nextState = state.nextState()
            if (state == nextState) {
                FinalStateWithCounter(state, counter + 1)
            } else {
                NotFinalStateWithCounter(nextState, counter + 1)
            }
        }.first { it is FinalStateWithCounter }
            .counter
}

sealed class StateWithCounter {
    abstract val state: SeaCucumbersState
    abstract val counter: Long
}

data class NotFinalStateWithCounter(
    override val state: SeaCucumbersState,
    override val counter: Long
) : StateWithCounter()

data class FinalStateWithCounter(
    override val state: SeaCucumbersState,
    override val counter: Long
) : StateWithCounter()