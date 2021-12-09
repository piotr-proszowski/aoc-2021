package eu.proszkie.adventofcode.day07

import eu.proszkie.adventofcode.WithResourceReadingAbility
import spock.lang.Specification

class CrabsAlignerSpec extends Specification implements WithResourceReadingAbility {

    CrabsAligner crabsAligner = new CrabsAligner()

    def "should properly calculate horizontal position"() {
        given:
        List<Integer> input = getResource(path).text.split(",").collect { it.toInteger() }

        when:
        def actual = crabsAligner.calculateOptimalHorizontalPosition(input)

        then:
        actual == expected

        where:
        path                           || expected
        '/advent-of-code/day07/input1' || 5
        '/advent-of-code/day07/input2' || 475
    }

    def "should properly calculate needed fuel to move into optimal horizontal position"() {
        given:
        List<Integer> input = getResource(path).text.split(",").collect { it.toInteger() }

        when:
        def actual = crabsAligner.calculateTheLeastFuelNeeded(input)

        then:
        actual == expected

        where:
        path                           || expected
        '/advent-of-code/day07/input1' || 168
        '/advent-of-code/day07/input2' || 96798233
    }
}
