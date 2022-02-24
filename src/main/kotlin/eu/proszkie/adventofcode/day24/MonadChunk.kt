package eu.proszkie.adventofcode.day24

class MonadChunk(private val instructions: List<Instruction>) {
    fun calculateRegistryState(digit: Int, inputRegistryState: RegistryState): RegistryState =
        instructions.fold(inputRegistryState) { state, instruction -> instruction.execute(state, digit) }

    init {
        require(instructions.count { it is InpInstruction } == 1) {
            "Monad chunk need to have exactly one input instruction"
        }
        require(instructions.firstOrNull() is InpInstruction) {
            "First instruction in chunk has to be input instruction"
        }
    }
}