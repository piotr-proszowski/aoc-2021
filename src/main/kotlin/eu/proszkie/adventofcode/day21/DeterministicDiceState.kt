package eu.proszkie.adventofcode.day21

data class DeterministicDiceState(val currentValue: Int, val countOfThrows: Int = 0) {
    fun next(): DeterministicDiceState {
        return copy(
            currentValue = if (currentValue == 1000) 1 else currentValue + 1,
            countOfThrows = countOfThrows + 1
        )
    }

    fun throwThreeTimes(): ThreeThrowsResult {
        return generateSequence(this, DeterministicDiceState::next)
            .drop(1)
            .take(3)
            .map { it to it.currentValue }
            .reduce { prev, next -> next.first to prev.second + next.second }
            .let { ThreeThrowsResult(it.first, it.second) }
    }
}

data class ThreeThrowsResult(val state: DeterministicDiceState, val sum: Int)