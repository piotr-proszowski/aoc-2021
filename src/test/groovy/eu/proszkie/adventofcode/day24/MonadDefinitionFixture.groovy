package eu.proszkie.adventofcode.day24

import eu.proszkie.adventofcode.WithResourceReadingAbility

class MonadDefinitionFixture implements WithResourceReadingAbility {
    static MonadDefinition loadFromFile(String path) {
        def instructions = new MonadDefinitionFixture()
                .readLines('/advent-of-code/day24/' + path)
                .collect { InstructionFactory.INSTANCE.fromString(it) }
        return MonadDefinition.@Companion.from(instructions)
    }
}
