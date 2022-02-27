package eu.proszkie.adventofcode.day25

import spock.lang.Specification

import static eu.proszkie.adventofcode.day25.SeaCucumbersStateFixture.cucumbersFromFile

class SeaCucumbersSimulatorSpec extends Specification {
    def 'should find after how many iterations there is no more possible transitions'() {
        expect:
            SeaCucumbersSimulator.INSTANCE.howManyIterationsToFinalState(initialState) == expected
        where:
            initialState                       || expected
            cucumbersFromFile('0_initial.txt') || 58L
            cucumbersFromFile('cucumbers.txt') || 563L
    }
}
