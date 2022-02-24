package eu.proszkie.adventofcode.day24

class ModelNumberFinder(private val monadDefinition: MonadDefinition) {

    val cache: MutableMap<DigitWihIndexAndRegistryState, Int> =
        object : LinkedHashMap<DigitWihIndexAndRegistryState, Int>(0, 0.75f, true) {
            override fun removeEldestEntry(eldest: MutableMap.MutableEntry<DigitWihIndexAndRegistryState, Int>?): Boolean {
                return size > 100_000
            }
        }
    var duplicates = 0

    fun findHighestValid(): ModelNumber =
        findHighestValid(9, 0, RegistryState.INITIAL)?.let(::ModelNumber)
            ?: throw IllegalStateException("not found")

    private fun findHighestValid(digit: Int, index: Int, inputRegistry: RegistryState): String? {
        val digitWithIndexAndRegistryState = DigitWihIndexAndRegistryState(digit, index, inputRegistry.hashCode())
        if (cache.contains(digitWithIndexAndRegistryState)) {
            cache[digitWithIndexAndRegistryState] = cache[digitWithIndexAndRegistryState]!! + 1
            duplicates += 1
            return null
        }
        cache[digitWithIndexAndRegistryState] = 1
        val newState = monadDefinition.calculateNthChunk(digit, index, inputRegistry)
        if (index == 13) {
            return if (newState.representsValidModelNumber()) {
                digit.toString()
            } else {
                null
            }
        }

        val nextIndex = if (index < 13) index + 1 else index

        return (9 downTo 1).firstNotNullOfOrNull {
            findHighestValid(it, nextIndex, newState)
        }?.let {
            digit.toString() + it
        }
    }
}

data class DigitWihIndexAndRegistryState(
    val digit: Int,
    val index: Int,
    val registry: Int
)