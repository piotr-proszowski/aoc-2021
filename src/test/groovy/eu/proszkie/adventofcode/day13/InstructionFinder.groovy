package eu.proszkie.adventofcode.day13

import eu.proszkie.adventofcode.WithResourceReadingAbility
import spock.lang.Specification

class InstructionFinder extends Specification implements WithResourceReadingAbility {

    def "should properly build page with instruction"() {
        given:
        List<String> input = readLines('/advent-of-code/day13/input1')

        when:
        Instruction instruction = InstructionFactory.INSTANCE.createFromStrings(input)

        then:
        instruction.dotsCount == 18
    }

    def "should properly fold page"() {
        given:
        List<String> input = readLines(path)
        Instruction instruction = InstructionFactory.INSTANCE.createFromStrings(input)
        List<Fold> folds = FoldsFactory.INSTANCE.createFromStrings(input)

        when:
        Instruction actual = instruction.fold(folds.first())

        then:
        actual.dotsCount == expected

        where:
        path                           || expected
        '/advent-of-code/day13/input1' || 17
        '/advent-of-code/day13/input2' || 602
    }

    def "should properly display folded page"() {
        given:
        List<String> input = readLines(path)
        Instruction instruction = InstructionFactory.INSTANCE.createFromStrings(input)
        List<Fold> folds = FoldsFactory.INSTANCE.createFromStrings(input)

        when:
        Instruction actual = instruction.fold(folds)

        then:
        actual.printPage()

        where:
        path                           || expected
        '/advent-of-code/day13/input2' || 602
    }
}
