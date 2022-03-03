package eu.proszkie.adventofcode.day11

import spock.lang.Specification

import static eu.proszkie.adventofcode.day11.OctopusesBuilder.fromFile
import static eu.proszkie.adventofcode.day11.OctopusesBuilder.octopuses

class OctopusesSpec extends Specification {
    def 'each octopus should increase energy during the step'() {
        given:
            Octopuses octopusesGrid = octopuses(
                    [0, 1, 2, 3],
                    [0, 1, 2, 3],
                    [0, 1, 2, 3],
            )
        when:
            Octopuses afterOneStep = octopusesGrid.nextStep()
        then:
            afterOneStep == octopuses(
                    [1, 2, 3, 4],
                    [1, 2, 3, 4],
                    [1, 2, 3, 4],
            )
    }

    def 'should propagate energy on flash'() {
        when:
            Octopuses afterOneStep = octopusesGrid.nextStep()
        then:
            afterOneStep.coordsToEnergyLevel == expected.coordsToEnergyLevel
        where:
            octopusesGrid      || expected
            fromFile('input3') || fromFile('input3-expected')
            /*
            2 3 4 8                 4 6 8 0
            1 8 9 5                 4 0 0 9
            0 9 7 6                 3 0 0 9
            */

    }

    def 'should handle properly multiple steps'() {
        given:
            Octopuses octopusesGrid = fromFile('input1')
            Octopuses expected = fromFile('input1-expected')
        when:
            Octopuses actual = (1..100).inject(octopusesGrid) { acc, _ -> acc.nextStep() }
        then:
            actual.coordsToEnergyLevel == expected.coordsToEnergyLevel
    }

    def 'should count flashes after 100 steps'() {
        when:
            Octopuses afterOneHundredSteps = (1..100).inject(octopusesGrid) { acc, _ -> acc.nextStep() }
        then:
            afterOneHundredSteps.amountOfFlashes == expected
        where:
            //@formatter:off
            octopusesGrid                                       || expected
            octopuses([2, 3, 4, 8],
                      [1, 8, 9, 5],
                      [0, 9, 7, 6])                             || 174
            fromFile('input0')                                  || 40
            fromFile('input1')                                  || 1656
            fromFile('input2')                                  || 1625
            //@formatter:on
    }

    def 'should find a step when all octopuses flashed'() {

        when:
            def actual = octopusesGrid.findStepWhenAllOctopusesFlash()
        then:
            actual == expected
        where:
            //@formatter:off
            octopusesGrid                                       || expected
            fromFile('input1')                                  || 195
            fromFile('input2')                                  || 244
            //@formatter:on
    }
}
