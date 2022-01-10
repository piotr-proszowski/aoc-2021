package eu.proszkie.adventofcode.day21

sealed class DiceState(open val currentValue: Int, open val countOfThrows: Int = 0) {

    fun throwThreeTimes(): List<ThreeThrowsResult> {
        return next().map { it to it.next() }.flatMap {
            val first = it.first
            it.second.flatMap {
                val second = it
                second.next().map { third -> listOf(first, second, third) }
            }
        }.map(this::sumUpValues)
            .map { ThreeThrowsResult(it.first, it.second) }
    }

    private fun sumUpValues(states: List<DiceState>) =
        states.map { it to it.currentValue }.reduce { prev, next -> next.first to prev.second + next.second }

    abstract fun next(): List<DiceState>
}

data class ThreeThrowsResult(val state: DiceState, val sum: Int)

data class DeterministicDiceState(override val currentValue: Int, override val countOfThrows: Int = 0) :
    DiceState(currentValue, countOfThrows) {
    override fun next(): List<DiceState> {
        return listOf(
            copy(
                currentValue = if (currentValue == 1000) 1 else currentValue + 1,
                countOfThrows = countOfThrows + 1
            )
        )
    }

}

data class DiracDice(override val currentValue: Int, override val countOfThrows: Int) : DiceState(currentValue, countOfThrows) {
    override fun next(): List<DiceState> {
        val firstNextValue = (currentValue + 1) % 3
        val secondNextValue = (currentValue + 2) % 3
        val thirdNextValue = (currentValue + 3) % 3
        return listOf(
            copy(currentValue = if (firstNextValue == 0) 3 else firstNextValue, countOfThrows = countOfThrows + 1),
            copy(currentValue = if (secondNextValue == 0) 3 else secondNextValue, countOfThrows = countOfThrows + 1),
            copy(currentValue = if (thirdNextValue == 0) 3 else thirdNextValue, countOfThrows = countOfThrows + 1),
        )
    }
}