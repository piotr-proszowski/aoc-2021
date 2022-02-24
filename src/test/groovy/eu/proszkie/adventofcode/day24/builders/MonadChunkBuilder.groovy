package eu.proszkie.adventofcode.day24.builders

import eu.proszkie.adventofcode.day24.Instruction
import eu.proszkie.adventofcode.day24.MonadChunk
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

import static eu.proszkie.adventofcode.day24.builders.InstructionBuilder.instruction

@Builder(builderStrategy = SimpleStrategy, prefix = '')
class MonadChunkBuilder {
    List<Instruction> instructions = []

    static MonadChunk monadChunk(@DelegatesTo(MonadChunkBuilder) Closure definition) {
        MonadChunkBuilder builder = new MonadChunkBuilder()
        builder.with(definition)
        return builder.build()
    }

    void anInstruction(String input) {
        instructions += instruction(input)
    }

    void withInstructions(List<Instruction> instructions) {
        this.instructions = instructions
    }

    private MonadChunk build() {
        return new MonadChunk(instructions)
    }
}
