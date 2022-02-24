package eu.proszkie.adventofcode.day24

import spock.lang.Specification

import static eu.proszkie.adventofcode.day24.builders.InstructionBuilder.instruction

class InstructionSpec extends Specification {
    def 'there is an add instruction'() {
        expect:
            instruction('add x y')
            instruction('add z 1')
            instruction('add w 16')
    }

    def 'there is a mul instruction'() {
        expect:
            instruction('mul x y')
            instruction('mul z -3')
            instruction('mul w 16')
    }

    def 'there is an eql instruction'() {
        expect:
            instruction('eql x y')
            instruction('eql z -3')
            instruction('eql w 16')
    }

    def 'there is a div instruction'() {
        expect:
            instruction('div x y')
            instruction('div z -3')
            instruction('div w 16')
    }

    def 'there is a mod instruction'() {
        expect:
            instruction('mod x y')
            instruction('mod z -3')
            instruction('mod w 16')
    }

    def 'there is an inp instruction'() {
        expect:
            instruction('inp x')
            instruction('inp y')
            instruction('inp w')
    }
}
