package eu.proszkie.adventofcode.day24

data class MonadDefinition(private val raw: List<MonadChunk>) {
    fun calculateNthChunk(digit: Int, index: Int, inputRegistryState: RegistryState): RegistryState =
        raw[index].calculateRegistryState(digit, inputRegistryState)

    companion object {
        fun from(instructions: List<Instruction>): MonadDefinition =
            instructions
                .fold(listOf<List<Instruction>>()) { chunks, instruction -> processInstruction(instruction, chunks) }
                .map(::MonadChunk)
                .let(::MonadDefinition)

        private fun processInstruction(instruction: Instruction, chunks: List<List<Instruction>>) =
            if (instruction is InpInstruction) {
                chunks.plusElement(listOf(instruction))
            } else {
                chunks.dropLast(1).plusElement(chunks.last().plus(instruction))
            }
    }
}