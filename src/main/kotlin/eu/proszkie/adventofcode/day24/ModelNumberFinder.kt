package eu.proszkie.adventofcode.day24

class ModelNumberFinder(private val monadDefinition: MonadDefinition) {

    private val cache: MutableMap<DigitWihIndexAndRegistryState, Int> =
        object : LinkedHashMap<DigitWihIndexAndRegistryState, Int>(0, 0.75f, true) {
            override fun removeEldestEntry(eldest: MutableMap.MutableEntry<DigitWihIndexAndRegistryState, Int>?): Boolean {
                return size > 1_000_000
            }
        }

    fun findHighestValid(): ModelNumber =
        (9 downTo 1).firstNotNullOfOrNull { digit ->
            println("Processing digit: $digit")
            findDesiredNumber(
                digit = digit,
                inputState = RegistryState.INITIAL,
                progression = 9 downTo 1
            )
        }?.let(::ModelNumber)
            ?: throw IllegalStateException("not found")

    fun findLowestValid(): ModelNumber =
        (1..9).firstNotNullOfOrNull { digit ->
            println("Processing digit: $digit")
            findDesiredNumber(
                digit = digit,
                inputState = RegistryState.INITIAL,
                progression = 1..9
            )
        }?.let(::ModelNumber)
            ?: throw IllegalStateException("not found")

    private fun findDesiredNumber(
        prefix: String = "",
        index: Int = 0,
        digit: Int,
        inputState: RegistryState,
        progression: IntProgression
    ): String? {
        if (cacheContains(digit, index, inputState)) return null
        val newState = monadDefinition.calculateNthChunk(digit, index, inputState)
        return findNext(index, newState, prefix, digit, progression)
    }

    private fun findNext(
        index: Int,
        newState: RegistryState,
        prefix: String,
        digit: Int,
        progression: IntProgression
    ): String? {
        return if (index == 13) {
            processLastIndex(newState, prefix, digit)
        } else {
            progression.firstNotNullOfOrNull { nextDigit ->
                findDesiredNumber(
                    prefix = prefix + digit.toString(),
                    digit = nextDigit,
                    index = index + 1,
                    inputState = newState,
                    progression = progression
                )
            }
        }
    }

    private fun processLastIndex(
        newState: RegistryState,
        prefix: String,
        digit: Int
    ) = if (newState.representsValidModelNumber()) prefix + digit.toString() else null

    private fun cacheContains(digit: Int, index: Int, inputState: RegistryState): Boolean {
        val digitWithIndexAndRegistryState = DigitWihIndexAndRegistryState(digit, index, inputState)
        if (cache.contains(digitWithIndexAndRegistryState)) {
            cache[digitWithIndexAndRegistryState] = cache[digitWithIndexAndRegistryState]!! + 1
            return true
        } else {
            cache[digitWithIndexAndRegistryState] = 1
        }
        return false
    }
}

data class DigitWihIndexAndRegistryState(
    val digit: Int,
    val index: Int,
    val registry: RegistryState
)