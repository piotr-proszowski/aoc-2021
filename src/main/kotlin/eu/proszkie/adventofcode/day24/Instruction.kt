package eu.proszkie.adventofcode.day24

sealed class Instruction(val outputRegistry: RegistryName) {
    fun execute(state: RegistryState, digit: Long? = null): RegistryState {
        return state.update(this, digit)
    }
}

sealed class WithoutInput(outputRegistry: RegistryName, val argument: RegistryNameOrPlainValue) :
    Instruction(outputRegistry) {
    abstract fun calculate(a: Long, b: Long): Long
}


class AddInstruction(first: RegistryName, val second: RegistryNameOrPlainValue) :
    WithoutInput(first, second) {
    override fun calculate(a: Long, b: Long): Long =
        a + b
}

class MulInstruction(first: RegistryName, val second: RegistryNameOrPlainValue) :
    WithoutInput(first, second) {
    override fun calculate(a: Long, b: Long): Long =
        a * b
}

class EqlInstruction(first: RegistryName, val second: RegistryNameOrPlainValue) :
    WithoutInput(first, second) {
    override fun calculate(a: Long, b: Long): Long =
        if (a == b) 1 else 0
}

class DivInstruction(first: RegistryName, val second: RegistryNameOrPlainValue) :
    WithoutInput(first, second) {
    override fun calculate(a: Long, b: Long): Long =
        a / b
}

class ModInstruction(first: RegistryName, val second: RegistryNameOrPlainValue) :
    WithoutInput(first, second) {
    override fun calculate(a: Long, b: Long): Long =
        a % b
}

class InpInstruction(val first: RegistryName) : Instruction(first)

data class RegistryName(val raw: Char)
sealed class RegistryNameOrPlainValue
data class JustRegistryName(val raw: RegistryName) : RegistryNameOrPlainValue()
data class JustPlainValue(val raw: PlainValue) : RegistryNameOrPlainValue()
