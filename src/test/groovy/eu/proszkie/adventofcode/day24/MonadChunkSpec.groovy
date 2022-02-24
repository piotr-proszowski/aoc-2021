package eu.proszkie.adventofcode.day24

import spock.lang.Specification

import static eu.proszkie.adventofcode.day24.builders.InstructionBuilder.instruction
import static eu.proszkie.adventofcode.day24.builders.MonadChunkBuilder.monadChunk

class MonadChunkSpec extends Specification {

    def 'there is a valid monad chunk'() {
        expect:
            monadChunk {
                anInstruction('inp x')
                anInstruction('add a b')
                anInstruction('add x 123')
            }
    }

    def 'monad chunk has exactly single input instruction'() {
        when:
            monadChunk {
                withInstructions(instructions)
            }
        then:
            thrown(IllegalArgumentException)
        where:
            instructions << [[], [instruction('inp x'), instruction('inp y')]]
    }

    def 'monad chunk has to start with input instruction'() {
        when:
            monadChunk {
                anInstruction('add a b')
                anInstruction('inp x')
            }
        then:
            thrown(IllegalArgumentException)
    }
}
