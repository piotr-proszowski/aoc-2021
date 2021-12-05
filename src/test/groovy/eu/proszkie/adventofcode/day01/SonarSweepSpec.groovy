package eu.proszkie.adventofcode.day01

import eu.proszkie.adventofcode.WithResourceReadingAbility
import spock.lang.Specification

class SonarSweepSpec extends Specification implements WithResourceReadingAbility {

    SonarSweep sonarSweep = new SonarSweep()

    def "should properly calculate amount of depth increases"() {
        given:
        def measurements = getResource('/advent-of-code/day01/input1').readLines()

        when:
        def amountOfIncreases = sonarSweep.howMuchIncreases(measurements)

        then:
        amountOfIncreases == 7
    }

    def "should properly calculate amount of windowed depth increases"() {
        given:
        def measurements = getResource('/advent-of-code/day01/input3').readLines()

        when:
        def amountOfIncreases = sonarSweep.howMuchIncreasesWindowed(measurements)

        then:
        amountOfIncreases == 1158
    }
}
