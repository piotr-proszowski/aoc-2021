package eu.proszkie.adventofcode.day24

sealed class Instruction(val outputRegistry: RegistryName) {
    fun execute(state: RegistryState, digit: Int? = null): RegistryState {
        return state.update(this, digit)
    }
}

sealed class WithoutInput(outputRegistry: RegistryName, val argument: RegistryNameOrPlainValue) :
    Instruction(outputRegistry) {
    abstract fun calculate(a: Int, b: Int): Int
}


class AddInstruction(private val first: RegistryName, val second: RegistryNameOrPlainValue) :
    WithoutInput(first, second) {
    override fun calculate(a: Int, b: Int): Int = a + b
}

class MulInstruction(private val first: RegistryName, val second: RegistryNameOrPlainValue) :
    WithoutInput(first, second) {
    override fun calculate(a: Int, b: Int): Int = a * b
}

class EqlInstruction(private val first: RegistryName, val second: RegistryNameOrPlainValue) :
    WithoutInput(first, second) {
    override fun calculate(a: Int, b: Int): Int = if (a == b) 1 else 0
}

class DivInstruction(private val first: RegistryName, val second: RegistryNameOrPlainValue) :
    WithoutInput(first, second) {
    override fun calculate(a: Int, b: Int): Int = a / b
}

class ModInstruction(private val first: RegistryName, val second: RegistryNameOrPlainValue) :
    WithoutInput(first, second) {
    override fun calculate(a: Int, b: Int): Int = a % b
}

class InpInstruction(val first: RegistryName) : Instruction(first)

data class RegistryName(val raw: Char)
sealed class RegistryNameOrPlainValue
data class JustRegistryName(val raw: RegistryName) : RegistryNameOrPlainValue()
data class JustPlainValue(val raw: PlainValue) : RegistryNameOrPlainValue()
