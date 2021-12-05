package eu.proszkie.adventofcode.day02

import eu.proszkie.adventofcode.WithResourceReadingAbility
import spock.lang.Specification

class PositionCalculatorSpec extends Specification implements WithResourceReadingAbility {
    PositionCalculator positionCalculator = new PositionCalculator()

    def "should properly calculate position"() {
        given:
        def moves = getResource('/advent-of-code/day02/input1').readLines()

        when:
        def position = positionCalculator.calculatePosition(moves)

        then:
        position.horizontal == 1906
        position.depth == 1021972
        position.horizontal * position.depth == 1947878632
    }
}
