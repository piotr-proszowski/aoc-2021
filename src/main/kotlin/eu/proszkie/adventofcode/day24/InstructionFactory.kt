package eu.proszkie.adventofcode.day24

object InstructionFactory {
    fun fromString(input: String): Instruction {
        val tokens = tokenize(input)
        val instructionToken = tokens[0]
        require(instructionToken is InstructionToken) { "Every instruction should start with one of keywords: [add, mul, eql, div, mod, inp]" }
        val registryToken = tokens[1]
        require(registryToken is RegistryToken) { "Second token in instruction should point to registry name (for example x, y, z or w)" }
        return when (instructionToken) {
            AddToken -> AddInstruction(RegistryName(registryToken.registryName), tokens[2].toRegistryNameOrPlainValue())
            MulToken -> MulInstruction(RegistryName(registryToken.registryName), tokens[2].toRegistryNameOrPlainValue())
            EqlToken -> EqlInstruction(RegistryName(registryToken.registryName), tokens[2].toRegistryNameOrPlainValue())
            DivToken -> DivInstruction(RegistryName(registryToken.registryName), tokens[2].toRegistryNameOrPlainValue())
            ModToken -> ModInstruction(RegistryName(registryToken.registryName), tokens[2].toRegistryNameOrPlainValue())
            InpToken -> InpInstruction(RegistryName(registryToken.registryName))
        }
    }

    private fun tokenize(input: String): List<Token> {
        return input.split(" ").map(Token.Companion::fromString)
    }
}

sealed class Token {
    fun toRegistryNameOrPlainValue(): RegistryNameOrPlainValue =
        when (this) {
            is RegistryToken -> JustRegistryName(RegistryName(this.registryName))
            is PlainValue -> JustPlainValue(PlainValue(this.raw))
            else -> throw IllegalArgumentException("Third token should point to registry or be a plain number")
        }

    companion object {
        fun fromString(input: String): Token =
            when {
                input == "add" -> AddToken
                input == "mul" -> MulToken
                input == "div" -> DivToken
                input == "eql" -> EqlToken
                input == "mod" -> ModToken
                input == "inp" -> InpToken
                input.length == 1 && input[0].isLetter() -> RegistryToken(input[0])
                input.toLongOrNull() != null -> PlainValue(input.toLong())
                else -> throw IllegalArgumentException()
            }
    }
}

data class RegistryToken(val registryName: Char) : Token()
data class PlainValue(val raw: Long) : Token()
sealed class InstructionToken : Token()
object AddToken : InstructionToken()
object MulToken : InstructionToken()
object EqlToken : InstructionToken()
object DivToken : InstructionToken()
object ModToken : InstructionToken()
object InpToken : InstructionToken()
