package eu.proszkie.adventofcode.day24

class ModelNumberValidator(private val monadDefinition: MonadDefinition) {
    fun validate(modelNumber: ModelNumber): RegistryState {
        val finalState = modelNumber.digits().withIndex().fold(RegistryState.INITIAL) { registry, digitWithIndex ->
            val (index, digit) = digitWithIndex
            monadDefinition.calculateNthChunk(digit, index, registry)
        }

        return finalState
    }
}