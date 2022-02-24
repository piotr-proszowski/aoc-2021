package eu.proszkie.adventofcode.day24

data class RegistryState(private val variables: Map<RegistryName, Int>) {
    operator fun get(registryName: Char): Int {
        return variables[RegistryName(registryName)]!!
    }

    fun update(instruction: Instruction, digit: Int?): RegistryState =
        when (instruction) {
            is WithoutInput -> handleWithoutInput(instruction)
            is InpInstruction -> copy(variables = variables + (instruction.outputRegistry to digit!!))
        }

    private fun handleWithoutInput(instruction: WithoutInput): RegistryState {
        val output = variables[instruction.outputRegistry]!!
        val result = when (instruction.argument) {
            is JustPlainValue -> instruction.calculate(output, instruction.argument.raw.raw)
            is JustRegistryName -> instruction.calculate(output, variables[instruction.argument.raw]!!)
        }

        return copy(
            variables = variables + (instruction.outputRegistry to result)
        )
    }

    fun representsValidModelNumber(): Boolean =
        variables[RegistryName('z')] == 0

    companion object {
        val INITIAL = RegistryState(mapOf(
            RegistryName('x') to 0,
            RegistryName('y') to 0,
            RegistryName('z') to 0,
            RegistryName('w') to 0
        ))
    }
}