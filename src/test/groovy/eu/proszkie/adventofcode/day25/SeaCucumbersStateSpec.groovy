package eu.proszkie.adventofcode.day25

import spock.lang.Specification

import static eu.proszkie.adventofcode.day25.SeaCucumbersStateFixture.cucumbersFromFile
import static eu.proszkie.adventofcode.day25.SeaCucumbersStateFixture.firstExpectedState
import static eu.proszkie.adventofcode.day25.SeaCucumbersStateFixture.firstInitialState
import static eu.proszkie.adventofcode.day25.SeaCucumbersStateFixture.secondExpectedState
import static eu.proszkie.adventofcode.day25.SeaCucumbersStateFixture.secondInitialState
import static eu.proszkie.adventofcode.day25.SeaCucumbersStateFixture.thirdExpectedState
import static eu.proszkie.adventofcode.day25.SeaCucumbersStateFixture.thirdInitialState

class SeaCucumbersStateSpec extends Specification {
    def 'should find next state properly'() {
        when:
            def actual = initialState.nextState()
        then:
            actual == expected
        where:
            initialState                       || expected
            firstInitialState()                || firstExpectedState()
            secondInitialState()               || secondExpectedState()
            thirdInitialState()                || thirdExpectedState()
            cucumbersFromFile('1_initial.txt') || cucumbersFromFile('1_expected.txt')
            cucumbersFromFile('2_initial.txt') || cucumbersFromFile('2_expected.txt')
    }
}
