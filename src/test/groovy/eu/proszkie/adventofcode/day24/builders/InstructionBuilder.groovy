package eu.proszkie.adventofcode.day24.builders

import eu.proszkie.adventofcode.day24.Instruction
import eu.proszkie.adventofcode.day24.InstructionFactory

class InstructionBuilder {

    static Instruction instruction(String input) {
        return InstructionFactory.INSTANCE.fromString(input)
    }
}
